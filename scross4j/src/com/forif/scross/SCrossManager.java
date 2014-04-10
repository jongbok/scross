/*
 * @(#) SCrossManager.java 1.0, 2014. 4. 2.
 * 
 * Copyright (c) 2014 Jong-Bok,Park  All rights reserved.
 */
 
package com.forif.scross;

import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.jackson.map.ObjectMapper;

import com.forif.scross.config.ConfigHandler;
import com.forif.scross.config.DefaultConfigHandler;
import com.forif.scross.encrypt.AESEncryptHandler;
import com.forif.scross.encrypt.EncryptHandler;
import com.forif.scross.generate.DefaultTockenGenerator;
import com.forif.scross.generate.InvalidTockenException;
import com.forif.scross.generate.TockenGenerator;

/**
 * @author Jong-Bok,Park (asdkf20@naver.com)
 * @version 1.0,  2014. 4. 2.
 * 
 */
public class SCrossManager {
	private static SCrossManager TOCKEN_MANAGER = null;
	private ObjectMapper mapper = new ObjectMapper();
	private Map<String, Map<String, Object>> config = null;
	private EncryptHandler encryptHandler = null;
	private ConfigHandler configHander = null;
	private TockenGenerator tockenGenerator = null;
	private ConcurrentHashMap<String, Map<String, Object>> repository = new ConcurrentHashMap<String, Map<String, Object>>();
	
	private SCrossManager(ConfigHandler configHandler, EncryptHandler encryptHandler, TockenGenerator tockenGenerator){
		this.configHander = configHandler;
		this.encryptHandler = encryptHandler;
		this.config = this.configHander.load();
		this.tockenGenerator = tockenGenerator;
		Thread collector = new Thread(new Collector());
		collector.start();
	}
	
	public static SCrossManager getInstance(){
		return getInstance(new DefaultConfigHandler());
	}
	
	public static SCrossManager getInstance(ConfigHandler configHandler){
		return getInstance(configHandler, new AESEncryptHandler());
	}
	
	public static SCrossManager getInstance(ConfigHandler configHandler, EncryptHandler encryptHandler){
		return getInstance(configHandler, encryptHandler, new DefaultTockenGenerator());
	}
	
	public static SCrossManager getInstance(ConfigHandler configHandler, EncryptHandler encryptHandler, TockenGenerator tockenGenerator){
		if(TOCKEN_MANAGER != null)
			return TOCKEN_MANAGER;
		else{
			TOCKEN_MANAGER = new SCrossManager(configHandler, encryptHandler, tockenGenerator);
			return TOCKEN_MANAGER;
		}
	}

	/**
	 * 토큰을 생성하고 토큰저장소에 보관한다. 
	 * @param secId
	 * @return
	 */
	public String generate(String secId){
		Map<String, Object> sec = getConfig(secId);
		byte[] key = (byte[]) sec.get("byte");
		String pattern = (String) sec.get("pattern");
		try {
			Map<String, Object> map = tockenGenerator.generate(System.currentTimeMillis(), pattern);
			String k = getRepositoryKey(secId, (String)map.get("body"));
			while(repository.get(k) != null){
				long current = System.currentTimeMillis();
				map = tockenGenerator.generate(current, pattern);
				k = getRepositoryKey(secId, (String)map.get("body"));
			}
			map.put("secId", secId);
			String tocken = mapper.writer().writeValueAsString(map);
//			System.out.println(tocken);
			String str = encryptHandler.encrypt(tocken, key);
			repository.putIfAbsent(k, map);
			return str;
		} catch (Exception e) {
			throw new SCrossException("cannot generate tocken!", e);
		}
	}
	
	private String getRepositoryKey(String secId, String body){
		return secId + "_" + body;
	}
	
	private Map<String, Object> getConfig(String secId){
		Map<String, Object> sec = config.get(secId);
		if(sec == null)
			throw new SCrossException("secId[" + secId + "] is not exists!");
		return sec;
	}
	
	/**
	 * Cross Site에서 생성된 토큰을 검증하고, 저장소에 보관한다.
	 * @param secId
	 * @param tocken
	 * @throws InvalidTockenException
	 */
	public void receive(String secId, String tocken) throws InvalidTockenException{
		Map<String, Object> sec = config.get(secId);
		byte[] key = (byte[]) sec.get("byte");
		int expire = ((Number)sec.get("expire")).intValue();
		String pattern = (String) sec.get("pattern");
		Map<String, Object> input = parse(tocken, key);
		tockenGenerator.validate(input, expire, pattern);
		String k = getRepositoryKey(secId, (String) input.get("body"));
		if(repository.get(k) != null)
			throw new InvalidTockenException("tocken is already exists in repository!");
		repository.putIfAbsent(k, input);
	}
	
	private Map<String, Object> parse(String tocken, byte[] key) throws InvalidTockenException{
		if(tocken == null || "".equals(tocken.trim()))
			throw new InvalidTockenException("tocken is empty");
		try {
			String str = encryptHandler.decrypt(tocken, key);
			return mapper.reader(Map.class).readValue(str);
		} catch (Exception e) {
			throw new InvalidTockenException("cannot parse json!", e);
		}		
	}
	
	/**
	 * CSRF공격 체크용으로 생성된 토큰을 삭제한다.
	 * 토큰이 저장소에 존재하지 않을경우 예외를 발생시킨다.
	 * @param secId
	 * @param tocken
	 * @throws InvalidTockenException
	 */
	public void destroy(String secId, String tocken) throws InvalidTockenException{
		Map<String, Object> sec = config.get(secId);
		byte[] key = (byte[]) sec.get("byte");
		int expire = ((Number)sec.get("expire")).intValue();
		String pattern = (String) sec.get("pattern");
		Map<String, Object> input = parse(tocken, key);
		tockenGenerator.validate(input, expire, pattern);
		String k = getRepositoryKey(secId, (String) input.get("body"));
		if(repository.get(k) == null)
			throw new InvalidTockenException("tocken is not exists!");
		repository.remove(k);
	}
	
	/**
	 * 저장소의 토큰을 강제삭제한다.
	 */
	public void clear(){
		repository.clear();
	}

	/**
	 * 매 1초마다 토큰저장소에 만료된 토큰을 삭제한다.
	 * @author Jong-Bok,Park (asdkf20@naver.com)
	 * @version 1.0,  2014. 4. 4.
	 * 
	 */
	class Collector implements Runnable{

		@Override
		public void run() {
			while(true){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new SCrossException("cannot collect expired tockens", e);
				}

				Enumeration<String> keys = repository.keys();
				String secId, key;
				Map<String, Object> tocken;
				long current = System.currentTimeMillis();
				while(keys.hasMoreElements()){
					key = keys.nextElement();
					tocken = repository.get(key);
					secId = (String) tocken.get("secId");
					Map<String, Object> sec = config.get(secId);
					int expire = ((Number) sec.get("expire")).intValue();
					long time = ((Number)tocken.get("time")).longValue();
					if(tockenGenerator.isExpireTocken(expire, current, time))
						repository.remove(key);
				}
			}
		}
		
	}
}
