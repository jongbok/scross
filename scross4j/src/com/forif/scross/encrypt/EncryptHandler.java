/*
 * @(#) EncryptHandler.java 1.0, 2014. 4. 3.
 * 
 * Copyright (c) 2014 Jong-Bok,Park  All rights reserved.
 */
 
package com.forif.scross.encrypt;
/**
 * @author Jong-Bok,Park (asdkf20@naver.com)
 * @version 1.0,  2014. 4. 3.
 * 
 */
public interface EncryptHandler {

	/**
	 * 암호화
	 * @param src
	 * @param key
	 * @return
	 */
	String encrypt(String src, byte[] key);
	
	/**
	 * 복호화
	 * @param src
	 * @param key
	 * @return
	 */
	String decrypt(String src, byte[] key);
	
	/**
	 * 대칭형키 생성
	 * @return
	 */
	byte[] generateKey();
}
