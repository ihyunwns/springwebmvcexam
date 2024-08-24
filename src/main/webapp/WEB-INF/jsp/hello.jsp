<%--JSTL 문법중 tagLib 라는 문법이 있음 --%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Home Page</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>
    <h1>Welcome to the Home Page!</h1>
    <p>This is a simple Spring MVC example with CSS.</p>
    <p>테스트</p>
</body>
</html>