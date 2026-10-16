<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>DB test</title>
</head>
<body>
	<h1>SQL Server 연결 확인</h1>

    <c:choose>
        <c:when test="${connectionSuccess}">
            <p>DB 연결에 성공했습니다.</p>
            <p>접속 데이터베이스: ${currentDatabase}</p>
            <p>접속 계정: ${loginName}</p>
        </c:when>

        <c:otherwise>
            <p>DB 연결에 실패했습니다.</p>
            <p>${errorMessage}</p>
        </c:otherwise>
    </c:choose>

</body>
</html>