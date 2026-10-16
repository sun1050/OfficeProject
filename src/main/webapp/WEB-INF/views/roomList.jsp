<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>공간 조회 | Office Mate</title>
	
	<!-- 모든 페이지 적용 기본 스타일 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/common.css">

    <!--헤더, 메뉴 등 레이아웃-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/layout.css">

    <!--reservation만 사용-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/reservation.css">
	
</head>
<body>
	<header class="header">
        <div class="header-inner">
            <a class="logo" href="${pageContext.request.contextPath}/main">
                Office Mate
            </a>

            <nav class="menu">
                <a href="${pageContext.request.contextPath}/main">메인</a>

                <c:choose>
                    <c:when test="${not empty sessionScope.loginMemberId}">
                        <span class="menu-member">
                            <c:out value="${sessionScope.loginMemberName}" />님
                        </span>

                        <a href="${pageContext.request.contextPath}/logout">
                            로그아웃
                        </a>
                    </c:when>

                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login">
                            로그인
                        </a>
                    </c:otherwise>
                </c:choose>
            </nav>
        </div>
    </header>

    <main class="container">
        <h1 class="page-title">이용 가능 공간 조회</h1>

        <%-- 날짜를 선택해 GET /rooms?useDate=날짜 요청을 보낸다. --%>
        <form class="date-search-form"
              method="get"
              action="${pageContext.request.contextPath}/rooms">

            <div>
                <label for="useDate">이용 날짜</label>

                <input type="date"
                       id="useDate"
                       name="useDate"
                       value="${useDate}"
                       min="${today}"
                       required>
            </div>

            <button type="submit" class="button button-primary">
                조회
            </button>
        </form>

        <%-- 예약 성공·실패 후 Redirect되어 온 한 번짜리 메시지 --%>
        <c:if test="${not empty flashMessage}">
            <p class="form-success">
                <c:out value="${flashMessage}" />
            </p>
        </c:if>

        <%-- 잘못된 날짜 입력 등 조회 오류 메시지 --%>
        <c:if test="${not empty errorMessage}">
            <p class="form-error">
                <c:out value="${errorMessage}" />
            </p>
        </c:if>

        <p class="result-description">
            <strong><c:out value="${useDate}" /></strong>에 이용 가능한 공간입니다.
        </p>

        <c:choose>
            <c:when test="${empty rooms}">
                <section class="empty-result">
                    <h2>이용 가능한 공간이 없습니다.</h2>
                    <p>다른 날짜를 선택해 다시 조회해 주세요.</p>
                </section>
            </c:when>

            <c:otherwise>
                <section class="room-grid">
                    <c:forEach var="room" items="${rooms}">
                        <article class="room-card">

                            <div class="room-image-box">
                                <img src="${pageContext.request.contextPath}${room.imagePath}"
                                     alt="${room.roomName} 공간 사진">
                            </div>

                            <div class="room-card-content">
                                <p class="room-type">
                                    <c:out value="${room.typeName}" />
                                </p>

                                <h2>
                                    <c:out value="${room.roomName}" />
                                </h2>

                                <p>
                                    <c:out value="${room.floorNo}" />층 ·
                                    최대 <c:out value="${room.maxCapacity}" />인
                                </p>

                                <%--
                                    로그인 회원:
                                    roomId와 useDate를 hidden input으로 전송해 예약 생성

                                    비로그인 사용자:
                                    로그인 페이지로 이동
                                --%>
                                <c:choose>
                                    <c:when test="${not empty sessionScope.loginMemberId}">
                                        <form method="post"
                                              action="${pageContext.request.contextPath}/reservation/create"
                                              class="room-reserve-form">

                                            <input type="hidden"
                                                   name="roomId"
                                                   value="${room.roomId}">

                                            <input type="hidden"
                                                   name="useDate"
                                                   value="${useDate}">

                                            <button type="submit"
                                                    class="button button-primary">
                                                예약하기
                                            </button>
                                        </form>
                                    </c:when>

                                    <c:otherwise>
                                        <a class="button button-primary room-login-button"
                                           href="${pageContext.request.contextPath}/login">
                                            로그인 후 예약
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </article>
                    </c:forEach>
                </section>
            </c:otherwise>
        </c:choose>
    </main>

    <footer class="footer">
        © 2026 Office Mate. Shared workspace reservation service.
    </footer>
</body>
</html>