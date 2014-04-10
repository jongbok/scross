/*
 * @(#) AESEncryptHandler.java 1.0, 2014. 4. 3.
 * 
 * Copyright (c) 2014 Jong-Bok,Park  All rights reserved.
 */
 
package com.forif.scross.encrypt;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.forif.scross.SCrossException;

/**
 * @author Jong-Bok,Park (asdkf20@naver.com)
 * @version 1.0,  2014. 4. 3.
 * 
 */
public class AESEncryptHandler implements EncryptHandler {
	
	/* (non-Javadoc)
	 * @see com.forif.scross.EncryptHandler#encrypt(java.lang.String, byte[])
	 */
	@Override
	public String encrypt(String src, byte[] key) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.encodeBase64String(cipher.doFinal(src.getBytes()));
		} catch (Exception e) {
			throw new SCrossException("cannot encrypt string!", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.forif.scross.EncryptHandler#decrypt(java.lang.String, byte[])
	 */
	@Override
	public String decrypt(String src, byte[] key) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.decodeBase64(src)));
		} catch (Exception e) {
			throw new SCrossException("cannot decrypt string!", e);
		}
	}

	/* (non-Javadoc)
	 * @see com.forif.scross.EncryptHandler#generateKey()
	 */
	@Override
	public byte[] generateKey() {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128);
			SecretKey key = kgen.generateKey();
			return key.getEncoded();
		} catch (NoSuchAlgorithmException e) {
			throw new SCrossException("cannot generate encrypt key!", e);
		}
	}

}
