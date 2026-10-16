<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<!--홈페이지-->
	
	<title>${pageTitle}</title>
	
	<!--모든 페이지 적용 기본 스타일-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/common.css?ver=2">

    <!--헤더, 메뉴 등 레이아웃-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/layout.css?ver=3">

    <!--메인 화면에서만 사용-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main.css?ver=2">
</head>
<body>
	<header class="header">
        <div class="header-inner">
            <a class="logo" href="${pageContext.request.contextPath}/main">
                Office Mate
            </a>

            <nav class="menu">
            
                <%-- 로그인 여부에 따라 메뉴를 다르게 표시한다. --%>
                <c:choose>
                    <c:when test="${not empty sessionScope.loginMemberId}">

                        <span class="menu-member">
                            <c:out value="${sessionScope.loginMemberName}" />님
                        </span>

                        <a href="${pageContext.request.contextPath}/reservation/my">
						    내 예약
						</a>

                        <%-- 관리자에게만 관리자 메뉴를 보여 준다. --%>
                        <c:if test="${sessionScope.loginMemberRole eq 'ADMIN'}">
                            <a href="${pageContext.request.contextPath}/admin">관리자</a>
                        </c:if>

                        <a href="${pageContext.request.contextPath}/logout">
                            로그아웃
                        </a>
                    </c:when>

                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login">
                            로그인
                        </a>

                        <a href="${pageContext.request.contextPath}/join">
                            회원가입
                        </a>
                    </c:otherwise>
                </c:choose>
            </nav>
        </div>
    </header>

    <main class="main-home">
        <section class="hero">
            <div class="hero-inner">
                <h1>필요한 날에, 필요한 공간을.</h1>

                <p>
                    날짜와 공간을 선택해
                    예약 가능한 공유오피스 공간을 확인하세요.
                </p>

                <a class="button button-white"
                   href="${pageContext.request.contextPath}/rooms">
                    공간 조회하기
                </a>
            </div>
        </section>
    </main>

    <footer class="footer">
        © 2026 Office Mate. Shared workspace reservation service.
    </footer>

</body>
</html>