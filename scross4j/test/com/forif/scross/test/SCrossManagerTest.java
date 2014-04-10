/*
 * @(#) SCrossManagerTest.java 1.0, 2014. 4. 3.
 * 
 * Copyright (c) 2014 Jong-Bok,Park  All rights reserved.
 */
 
package com.forif.scross.test;

import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Before;
import org.junit.Test;

import com.forif.scross.SCrossException;
import com.forif.scross.SCrossManager;
import com.forif.scross.config.ConfigHandler;
import com.forif.scross.generate.InvalidTockenException;

/**
 * @author Jong-Bok,Park (asdkf20@naver.com)
 * @version 1.0,  2014. 4. 3.
 * 
 */
public class SCrossManagerTest {

	private SCrossManager scross = null;
	private final static String SEC_ID = "portal_GW";
	private final static int EXPIRE = 3;
	private final static String ENCRYPT_KEY = "a9f44db99281ac5391093840d1e21326";
	private final static String PATTERN = "from$C[48-497]por$C[2001-3040]tal$C[56-4006]to$C[5329-10253]GW";
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		scross = SCrossManager.getInstance(new ConfigHandler() {
			
			@Override
			public void save(Map<String, Map<String, Object>> config) { }
			
			@Override
			public Map<String, Map<String, Object>> load() {
				Map<String, Map<String, Object>> config = new HashMap<String, Map<String,Object>>();
				Map<String, Object> sec = new HashMap<String, Object>();
				sec.put("key", ENCRYPT_KEY);
				try {
					sec.put("byte", Hex.decodeHex(ENCRYPT_KEY.toCharArray()));
				} catch (DecoderException e) {
					throw new SCrossException("cannot load config!", e);
				}
				sec.put("pattern", PATTERN);
				sec.put("expire", EXPIRE);
				config.put(SEC_ID, sec);
				return config;
			}
		});
	}

	/**
	 * tocken 정상 발급및 이중발행여부
	 */
	@Test
	public void testGenerate(){
		String tocken; 
		Map<String, String> map = new Hashtable<String, String>();
		for(int i=0; i<1000; i++){
			tocken = scross.generate(SEC_ID);
//			System.out.println(">> " + tocken);
			assertNull(map.get(tocken));
			map.put(tocken, "exists");
		}
	}
	
	/**
	 * cross site에서 발행된 tocken 수신
	 * @throws InvalidTockenException
	 */
	@Test
	public void testReceive() throws InvalidTockenException{
		String tocken = scross.generate(SEC_ID);
		scross.clear();
		scross.receive(SEC_ID, tocken);
	}
	
	/**
	 * cross site에서  발행된 tocken 중복수신 여부
	 * @throws InvalidTockenException
	 */
	@Test(expected = InvalidTockenException.class)
	public void testReceiveTwice() throws InvalidTockenException{
		String tocken = scross.generate(SEC_ID);
		scross.clear();
		scross.receive(SEC_ID, tocken);
		scross.receive(SEC_ID, tocken);
	}

	/**
	 * CSRF방어용 발행한 tocken 확인/제거
	 * @throws InvalidTockenException
	 */
	@Test
	public void testDestory() throws InvalidTockenException{
		String tocken = scross.generate(SEC_ID);
		scross.destroy(SEC_ID, tocken);
	}
	
	/**
	 * tocken 위변조 여부
	 * @throws InvalidTockenException
	 */
	@Test(expected = InvalidTockenException.class)
	public void testTockenForge1() throws InvalidTockenException{
		String tocken = scross.generate(SEC_ID);
		String forge = "a" + tocken;
		scross.destroy(SEC_ID, forge);
	}
	
	/**
	 * tocken 위변조 여부
	 * @throws InvalidTockenException
	 */
	@Test(expected = InvalidTockenException.class)
	public void testTockenForge2() throws InvalidTockenException{
		String tocken = scross.generate(SEC_ID);
		String forge = tocken.substring(0, 5) + "a" + tocken.substring(6, 11) + "b" + tocken.substring(12);
		scross.destroy(SEC_ID, forge);
	}

	/**
	 * CSRF방어용 발행한 tocken 중복제거 여부
	 * @throws InvalidTockenException
	 */
	@Test(expected = InvalidTockenException.class)
	public void testDestroyTwice() throws InvalidTockenException {
		String tocken = scross.generate(SEC_ID);
		scross.destroy(SEC_ID, tocken);
		scross.destroy(SEC_ID, tocken);
	}

	/**
	 * tocken 만료여부
	 * @throws InterruptedException
	 * @throws InvalidTockenException
	 */
	@Test(expected = InvalidTockenException.class)
	public void testTockenExpire() throws InterruptedException, InvalidTockenException{
		String tocken = scross.generate(SEC_ID);
		Thread.sleep((EXPIRE+1) * 1000);
		scross.destroy(SEC_ID, tocken);
	}

}
