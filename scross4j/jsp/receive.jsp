<%@page import="com.forif.scross.SCrossManager"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String tocken = request.getParameter("scrossTocken");
	SCrossManager scrossManager = SCrossManager.getInstance();
	scrossManager.receive("portal_GW", tocken);  // 검증실패시 InvalidTockenException 발생됨
	
	//TODO 주문서 처리
%>
