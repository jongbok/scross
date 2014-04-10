/*
 * @(#) DefaultTockenGenerator.java 1.0, 2014. 4. 3.
 * 
 * Copyright (c) 2014 Jong-Bok,Park  All rights reserved.
 */
 
package com.forif.scross.generate;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.forif.scross.SCrossException;

/**
 * @author Jong-Bok,Park (asdkf20@naver.com)
 * @version 1.0,  2014. 4. 3.
 * 
 */
public class DefaultTockenGenerator implements TockenGenerator {

	public final static Pattern EXP_PATTERN = Pattern.compile("\\$[cC]\\[\\d+\\-\\d+\\]");

	/**
	 * 토큰 body생성
	 * @param time
	 * @param pattern
	 * @return
	 */
	private String generateBody(long time, String pattern){
		StringBuffer buffer = new StringBuffer();
		Matcher matcher = EXP_PATTERN.matcher(pattern);
		while(matcher.find()){
			String group = matcher.group();
			String text = group.substring(3, group.length()-1);
			StringTokenizer st = new StringTokenizer(text, "-");
			int start = Integer.parseInt(st.nextToken());
			int end = Integer.parseInt(st.nextToken());
			if(start < Character.MIN_VALUE || start > Character.MAX_VALUE)
				throw new IllegalArgumentException(group + " start is not valid!");
			if(end < Character.MIN_VALUE || end > Character.MAX_VALUE)
				throw new IllegalArgumentException(group + " end is not valid!");
			int gap = Math.abs(end - start);
			int r = (int)(start + (time%gap));
			char c = r == 92? (char)93: (char)r;
			try{
				matcher.appendReplacement(buffer, String.valueOf(c));
			}catch(IndexOutOfBoundsException e){
				throw new SCrossException(r + "(" + c + ") is not allow char!", e);
			}
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.forif.scross.generate.TockenGenerator#generate(long, java.lang.String)
	 */
	public Map<String, Object> generate(long time, String pattern){
		String body = generateBody(time, pattern);
		Map<String, Object> tocken = new HashMap<String, Object>();
		tocken.put("time", time);
		tocken.put("body", body);
		return tocken;
	}

	/* (non-Javadoc)
	 * @see com.forif.scross.generate.TockenGenerator#validate(java.util.Map, int, java.lang.String)
	 */
	public void validate(Map<String, Object> tocken, int expire, String pattern) throws InvalidTockenException{
		long current = System.currentTimeMillis();
		long time = ((Number)tocken.get("time")).longValue();
		String inBody = (String) tocken.get("body");
		if(isExpireTocken(expire, current, time))
			throw new InvalidTockenException("tocken is expired!");
		Map<String, Object> gen = generate(time, pattern);
		String genBody = (String) gen.get("body");
		if(!inBody.equals(genBody))
			throw new InvalidTockenException("invalid tocken!");
	}
	
	/* (non-Javadoc)
	 * @see com.forif.scross.generate.TockenGenerator#isExpireTocken(int, long, long)
	 */
	public boolean isExpireTocken(int expire, long current, long time){
		return (time < (current - expire*1000)) 
				|| (time > (current + expire*1000));
	}	
}
