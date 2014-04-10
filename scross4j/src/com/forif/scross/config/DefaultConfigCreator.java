/*
 * @(#) DefaultConfigCreator.java 1.0, 2014. 4. 4.
 * 
 * Copyright (c) 2014 Jong-Bok,Park  All rights reserved.
 */
 
package com.forif.scross.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;

import org.apache.commons.codec.binary.Hex;

import com.forif.scross.SCrossException;
import com.forif.scross.encrypt.AESEncryptHandler;
import com.forif.scross.encrypt.EncryptHandler;
import com.forif.scross.generate.DefaultTockenGenerator;

/**
 * @author Jong-Bok,Park (asdkf20@naver.com)
 * @version 1.0,  2014. 4. 4.
 * 
 */
public class DefaultConfigCreator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ConfigHandler configHandler = new DefaultConfigHandler();
		EncryptHandler encryptHandler = new AESEncryptHandler();
		Map<String, Map<String, Object>> config = null;
		try{
			config = configHandler.load();
		}catch(SCrossException e){
			config = new HashMap<String, Map<String,Object>>();
		}
		Scanner scanner = new Scanner(System.in);
		boolean isModify = false;
		while(true){
			char cmd = getCommand(scanner);
			switch(cmd){
				case 'c':
					String secId = getSecId(scanner);
					String pattern = getPattern(scanner);
					int expire = getExpire(scanner);
					String key = Hex.encodeHexString(encryptHandler.generateKey());
					Map<String, Object> sec = new HashMap<String, Object>(3);
					sec.put("pattern", pattern);
					sec.put("key", key);
					sec.put("expire", expire);
					config.put(secId, sec);
					isModify = true;
					break;
				case 's':
					configHandler.save(config);
					isModify = false;
					break;
				case 'l':
					listConfig(config);
					break;
				case 'q':
					if(isModify && isConfirm(scanner))
						configHandler.save(config);
					System.exit(0);
					break;
				default:
					System.err.println("command is not valid!");
					continue;
			}
		}

	}
	
	/**
	 * 변경된 내용이 있을경우 확인
	 * @param scanner
	 * @return
	 */
	private static boolean isConfirm(Scanner scanner){
		System.out.print("Configuration is modified! Are you save it?(y/n): ");
		String confirm = scanner.nextLine();
		if(isEmpty(confirm))
			return false;
		return "y".equals(confirm);
	}
	
	/**
	 * 설정정보 조회
	 * @param config
	 */
	private static void listConfig(Map<String, Map<String, Object>> config){
		Iterator<String> keys = config.keySet().iterator();
		String key;
		Map<String, Object> map;
		System.out.format("+----------+----------------------------------------+------+----------------------------------------------------------------------------------------------------+%n");
		System.out.format("| secId    |   key                                  |expire|  pattern                                                                                           |%n");
		System.out.format("+----------+----------------------------------------+------+----------------------------------------------------------------------------------------------------+%n");
		while(keys.hasNext()){
			key = keys.next();
			map = config.get(key);
			System.out.format("| %-8s | %-38s | %4d | %-98s |%n", key, map.get("key"), map.get("expire"), map.get("pattern"));
		}
		System.out.format("+----------+----------------------------------------+------+----------------------------------------------------------------------------------------------------+%n");
	}
	
	/**
	 * 만료시간 입력
	 * @param scanner
	 * @return
	 */
	private static int getExpire(Scanner scanner){
		System.out.print("expire(default 60 seconds): ");
		String expire = scanner.nextLine();
		if(isEmpty(expire))
			return 60;
		try{
			return Integer.parseInt(expire);
		}catch(NumberFormatException e){
			System.err.println("input number!");
			return getExpire(scanner);
		}
	}
	
	/**
	 * pattern 입력
	 * @param scanner
	 * @return
	 */
	private static String getPattern(Scanner scanner){
		System.out.print("pattern($C[0-65535]): ");
		String pattern = scanner.nextLine();
		if(isEmpty(pattern)){
			System.err.println("pattern is required!");
			return getPattern(scanner);
		}
		Matcher matcher = DefaultTockenGenerator.EXP_PATTERN.matcher(pattern);
		int cnt = 0;
		while(matcher.find())
			cnt++;
		if(cnt < 3){
			System.err.println("input pattern least three times!");
			return getPattern(scanner);
		}
		return pattern;
	}
	
	/**
	 * secId 입력
	 * @param scanner
	 * @return
	 */
	private static String getSecId(Scanner scanner){
		System.out.print("secId(allow Alpabetic,Digit,'_'): ");
		String secId = scanner.nextLine();
		if(isEmpty(secId)){
			System.err.println("secId is required!");
			return getSecId(scanner);
		}
		char[] c = secId.toCharArray();
		for(int i=0; i<c.length; i++){
			if(Character.isAlphabetic(c[i]))
				continue;
			if(Character.isDigit(c[i]))
				continue;
			if(c[i] == '_')
				continue;
			System.err.println("secId allow Alpabetic,Digit,'_'");
			return getSecId(scanner);
		}
		return secId;
	}
	
	/**
	 * command 입력
	 * @param scanner
	 * @return
	 */
	private static char getCommand(Scanner scanner){
		System.out.print("command(c:create config, s:save config, l:list config, q:exit): ");
		String cmd = scanner.nextLine();
		if(isEmpty(cmd)){
			System.err.println("command is required!");
			return getCommand(scanner);
		}
		if(cmd.length() != 1){
			System.err.println("input single char!");
			return getCommand(scanner);
		}
		return cmd.charAt(0);
	}
	
	private static boolean isEmpty(String str){
		return str == null || "".equals(str.trim());
	}

}
