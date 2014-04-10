/*
 * @(#) ConfigHandler.java 1.0, 2014. 4. 2.
 * 
 * Copyright (c) 2014 Jong-Bok,Park  All rights reserved.
 */
 
package com.forif.scross.config;

import java.util.Map;

/**
 * @author Jong-Bok,Park (asdkf20@naver.com)
 * @version 1.0,  2014. 4. 2.
 * 
 */
public interface ConfigHandler {

	/**
	 * 환경설정정보를 적제한다.
	 * @return
	 */
	Map<String, Map<String, Object>> load();
	
	/**
	 * 환경설정정보를 보관한다.
	 * @param config
	 */
	void save(Map<String, Map<String, Object>> config);
}
