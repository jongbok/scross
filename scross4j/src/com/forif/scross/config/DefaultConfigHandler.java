/*
 * @(#) DefaultConfigHandler.java 1.0, 2014. 4. 2.
 * 
 * Copyright (c) 2014 Jong-Bok,Park  All rights reserved.
 */
 
package com.forif.scross.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.codehaus.jackson.map.ObjectMapper;

import com.forif.scross.SCrossException;

/**
 * @author Jong-Bok,Park (asdkf20@naver.com)
 * @version 1.0,  2014. 4. 2.
 * 
 */
public class DefaultConfigHandler implements ConfigHandler {

	private ObjectMapper mapper = new ObjectMapper();
	private File file = null;
	/**
	 * 
	 */
	public DefaultConfigHandler(File file) {
		this.file = file;
	}
	
	public DefaultConfigHandler(){
		URL url = Thread.currentThread().getContextClassLoader().getResource(".");
		this.file = new File(url.getPath(), "s-cross.json");
	}
	
	/* (non-Javadoc)
	 * @see com.forif.scross.ConfigHandler#load()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, Object>> load(){
		InputStream in = null;
		try {
			if(!file.exists())
				throw new SCrossException("config file is not exists!");
			in = new FileInputStream(file);
			Map<String, Map<String, Object>> config = mapper.readValue(in, Map.class);
			Iterator<String> keys = config.keySet().iterator();
			Map<String, Object> sec;
			String key, k;
			byte[] kb;
			while(keys.hasNext()){
				key = keys.next();
				sec = config.get(key);
				k = (String) sec.get("key");
				kb = Hex.decodeHex(k.toCharArray());
				sec.put("byte", kb);
			}
			return config;
		} catch (IOException e) {
			throw new SCrossException("cannot read config!", e);
		} catch (DecoderException e) {
			throw new SCrossException("cannot read config!", e);
		} finally{
			if(in != null) try{ in.close(); }catch(IOException e){};
		}
		
	}
	
	/* (non-Javadoc)
	 * @see com.forif.scross.ConfigHandler#save(java.util.Map)
	 */
	@Override
	public void save(Map<String, Map<String, Object>> config){
		ObjectMapper mapper = new ObjectMapper();
		URL url = Thread.currentThread().getContextClassLoader().getResource(".");
		System.out.println(url.getPath());
		File f = new File(url.getPath(), "s-cross.json");
		FileOutputStream out = null;
		try{
			out = new FileOutputStream(f);
			mapper.writerWithDefaultPrettyPrinter().writeValue(out, config);
		} catch (Exception e) {
			throw new SCrossException("cannot write configuration!", e);
		}finally{
			if(out != null) try{ out.close(); }catch(IOException e){}
		}		
	}

}
