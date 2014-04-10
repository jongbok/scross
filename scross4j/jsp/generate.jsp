<%@page import="com.forif.scross.SCrossManager"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	SCrossManager scrossManager = SCrossManager.getInstance();
	String tocken = scrossManager.generate("portal_GW");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>A Site</title>
</head>
<body>
<form name="scrossForm" action="http://www.crossdomain.com/receive.jsp" method="post" >
	<input type="hidden" name="scrossTocken" value="<%= tocken %>" />
	상품코드: <input type="text" name="prodCd" /><br/>
	수량: <input type="text" name="count" /><br/>
	
	<input type="submit" value="주문" />
</form>
</body>
</html>