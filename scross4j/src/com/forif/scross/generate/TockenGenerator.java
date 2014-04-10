/*
 * @(#) TockenGenerator.java 1.0, 2014. 4. 3.
 * 
 * Copyright (c) 2014 Jong-Bok,Park  All rights reserved.
 */
 
package com.forif.scross.generate;

import java.util.Map;

/**
 * @author Jong-Bok,Park (asdkf20@naver.com)
 * @version 1.0,  2014. 4. 3.
 * 
 */
public interface TockenGenerator {

	/**
	 * 토큰생성
	 * @param time
	 * @param pattern
	 * @return
	 */
	Map<String, Object> generate(long time, String pattern);
	
	/**
	 * 토큰검증
	 * @param tocken
	 * @param expire
	 * @param pattern
	 * @throws InvalidTockenException
	 */
	void validate(Map<String, Object> tocken, int expire, String pattern) throws InvalidTockenException;
	
	/**
	 * 토큰만료 여부
	 * @param expire
	 * @param current
	 * @param time
	 * @return
	 */
	boolean isExpireTocken(int expire, long current, long time);
}
