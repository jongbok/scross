/*
 * @(#) SCrossException.java 1.0, 2014. 4. 2.
 * 
 * Copyright (c) 2014 Jong-Bok,Park  All rights reserved.
 */
 
package com.forif.scross;
/**
 * @author Jong-Bok,Park (asdkf20@naver.com)
 * @version 1.0,  2014. 4. 2.
 * 
 */
public class SCrossException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SCrossException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public SCrossException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public SCrossException(String arg0) {
		super(arg0);
	}

	public SCrossException(Throwable arg0) {
		super(arg0);
	}

}
