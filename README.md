S-Cross(Cross-Site Request Security)
======

###소개
 * S-Cross는 Cross-Site Request 검증을 목적으로 개발되었습니다.
 * S-Cross는 JRE1.6 이상에서 사용할 수 있도록 구현되어 있습니다.(다른 언어로 구현가능)
 * S-Cross는 SSO를 사용할 수 없는 여건에서 Cross Site간에 최소한의 인증을 보장해 줄 수 있는 방안을 제공합니다.
 * S-Cross는 CSRF(Cross-Site request forgery)공격 방어용으로 사용할 수 있습니다.
 * S-Cross는 Cross-Site간에 단순한 구조로 손쉽게 사용할 수 있도록 설계되어 추가적인 인프라 투자가 필요 없습니다.
 
###활용범위
 * SSO 미구축으로 인해 쉽고 빠르게 Cross-Site간의 인증이 필요한 경우.
 * Cross Site A,B를 인증받은 사용자가 A Site에서 정상적인 프로세스를 완료후 B Site를 호출여부를 검증이 필요할 경우.
 * 소프트웨어 납품시 Cross-Site간에 인증이 필요하지만 SSO Agent 라이선스 비용부담을 줄이고자 할때.
 * S-Cross는 SSO의 대체수단이 되지는 못하지만 SSO를 사용할 수 없을때 좋은선택이 될 수 있습니다.
 
###라이선스
 * 이 소프트웨어는 집,회사등에서 무료로 자유롭게 사용할 수 있는 자유소프트웨어이다.
 * 이 소프트웨어는 오픈소스(GPL3.0) 라이선스를 준수한다. 
 
###요구조건
 * Cross Site A,B는 동일한 인증을 위한 최소한의 동일한 환경설정이 존재한다.
 * A Site에서 생성된 검증용 토큰은 B Site에서 인증검증 되어야 한다.
 * 검증용 토큰은 불법적으로 취득되어 위변조가 어려워야 한다.
 * 동일한 토큰은 재사용 될 수 없다.
 * 인프라투자 없이 쉽고 빠르게 사용되어야 한다.
 
###명세서
 * 환경설정

| 항목     | 설명              | 예시                                                           |
|----------|-------------------|----------------------------------------------------------------|
| secId    | 검증 기준 식별자  | portal_GW                                                      |
| pattern  | 임의의 문자열     | from$C[48-497]por$C[2001-3040]tal$C[56-4006]to$C[5329-10253]GW |
| key      | 대칭형 암호화 key | a9f44db99281ac5391093840d1e21326                               |
| expire    |만료시간(second)  | 60                                                             |

 * Tocken

| 항목    | 설명                       | 예시                        |
|---------|----------------------------|-----------------------------|
|secId    |검증 기준 식별자            |portal_GW                    |
|body     |임의의 문자열               |from¢porࢦtalߒtoᗁGW           |
|time     |토큰생성시간(milli seconds) |1396598029896|               |
 * Tocken 생성

![alt tag](http://dev.naver.com/wiki/s-cross/pds/FrontPage/tocken_generate.png)
 * Tocken 검증
  * 만료여부 검증
    * Tocken Time < Current Time – Expiration Time = Expired Tocken
    * Tocken Time > Current Time + Expiration Time = Expired Tocken
    * Cross-Site A,B간 시스템시간의 차이가 만료시간을 초과할 경우 100% 검증실패됨.

![alt tag](http://dev.naver.com/wiki/s-cross/pds/FrontPage/tocken_expire_1.png)
  * 위변조여부 검증
    * 검증용 문자열 생성 = Tocken Time + Pattern
    * 검증용 문자열과 Tocken Body비교검증
  * 재사용여부 검증
    * 검증이 완료된 토큰은 토큰저장소에 저장된다.
    * 토큰저장소에 동일한 토큰이 존재할 경우 검증에 실패한다.
    * 토큰저장소에 저장된 토큰은 매초마다 만료여부를 검사하여 만료된 토큰은 삭제한다.

###설치방법
 * scross4j-*.zip을 다운로드 받은 후 압축을 해제한다.
 * start.bat을 실행해서 환경설정을 수행하면 s-cross.json 파일이 생성된다.
 * 생성된 s-cross.json을 인증하고자 하는 Cross-Site A,B의 Classpath에 배포한다.
 * lib 폴더 하위의 jar파일을 Cross-Site A,B의 Classpath에 배포한다.
 * 만료시간을 너무 크게 설정하고 동시접속자가 많을경우 OOM(Out Of Memory)를 발생시킬 수 있으니 주의필요.
 * pattern의 치환문자열의 범위는 1-65535 사이의 숫자이다.

![alt tag](http://dev.naver.com/wiki/s-cross/pds/FrontPage/config_generate.png)

###사용법
 * 인증대상 Site JSP(Source)
```
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
```
 * 인증수행 Site JSP(Target)
```
<%@page import="com.forif.scross.SCrossManager"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String tocken = request.getParameter("scrossTocken");
	SCrossManager scrossManager = SCrossManager.getInstance();
	scrossManager.receive("portal_GW", tocken);  // 검증실패시 InvalidTockenException 발생됨
	
	//TODO 주문서 처리
%>
```

###관련정보 링크
 * http://blog.naver.com/asdkf20 [Blog]
 * http://dev.naver.com/projects/rclog [RCLog4j Runtime Configuration Log4j]
 * http://dev.naver.com/projects/noti-j [noti-J Message Push]

