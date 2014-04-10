/*
 * @(#) InvalidTockenException.java 1.0, 2014. 4. 2.
 * 
 * Copyright (c) 2014 Jong-Bok,Park  All rights reserved.
 */
 
package com.forif.scross.generate;
/**
 * 토큰검증 예외
 * @author Jong-Bok,Park (asdkf20@naver.com)
 * @version 1.0,  2014. 4. 2.
 * 
 */
public class InvalidTockenException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1333864582022384205L;

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public InvalidTockenException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public InvalidTockenException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public InvalidTockenException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public InvalidTockenException(Throwable arg0) {
		super(arg0);
	}

}
