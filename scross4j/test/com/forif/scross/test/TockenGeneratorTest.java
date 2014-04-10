/*
 * @(#) TockenGeneratorTest.java 1.0, 2014. 4. 3.
 * 
 * Copyright (c) 2014 Jong-Bok,Park  All rights reserved.
 */
 
package com.forif.scross.test;

import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.forif.scross.generate.DefaultTockenGenerator;
import com.forif.scross.generate.TockenGenerator;

public class TockenGeneratorTest {

	private TockenGenerator generator = null;
	private final static String PATTERN = "from$C[48-497]por$C[2001-3040]tal$C[56-4006]to$C[5329-10253]GW";
	
	@Before
	public void setUp() throws Exception {
		generator = new DefaultTockenGenerator();
	}
	
	@Test
	public void testGenerate(){
		for(int i = Character.MIN_VALUE; i<=Character.MAX_VALUE; i++){
			try{
				Map<String,Object> tocken = generator.generate(i, PATTERN);
				assertNotNull(tocken);
			}catch(Exception e){
				System.err.println(e.getMessage());
			}
		}
	}


}
