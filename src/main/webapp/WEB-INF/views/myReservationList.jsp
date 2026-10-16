<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>내 예약 | Office Mate</title>
	
	<!-- 모든 페이지 적용 기본 스타일 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/common.css">

    <!--헤더, 메뉴 등 레이아웃-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/layout.css">

    <!--reservation만 사용-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/myReservation.css">
	
</head>
<body>
	<header class="header">
        <div class="header-inner">
            <a class="logo" href="${pageContext.request.contextPath}/main">
                Office Mate
            </a>

            <nav class="menu">
                <a href="${pageContext.request.contextPath}/rooms">공간 조회</a>
                <a href="${pageContext.request.contextPath}/logout">로그아웃</a>
            </nav>
        </div>
    </header>

    <main class="container">
        <h1 class="page-title">내 예약</h1>

        <c:if test="${not empty flashMessage}">
            <p class="form-success">
                <c:out value="${flashMessage}" />
            </p>
        </c:if>

        <c:choose>
            <c:when test="${empty reservations}">
                <section class="empty-result">
                    <h2>예약 내역이 없습니다.</h2>
                    <p>공간을 조회하고 원하는 날짜에 예약해 보세요.</p>

                    <a class="button button-primary"
                       href="${pageContext.request.contextPath}/rooms">
                        공간 조회하기
                    </a>
                </section>
            </c:when>

            <c:otherwise>
                <section class="reservation-list">
                    <c:forEach var="reservation" items="${reservations}">
                        <article class="reservation-card">

                            <div>
                                <p class="reservation-type">
                                    <c:out value="${reservation.typeName}" />
                                </p>

                                <h2>
                                    <c:out value="${reservation.roomName}" />
                                </h2>

                                <p class="reservation-detail">
                                    <c:out value="${reservation.floorNo}" />층 ·
                                    <c:out value="${reservation.useDate}" />
                                </p>
                            </div>

                            <div class="reservation-action">
                                <c:choose>
                                    <c:when test="${reservation.canceled}">
                                        <span class="status-canceled">
                                            취소됨
                                        </span>
                                    </c:when>

                                    <c:otherwise>
                                        <span class="status-active">
                                            예약 완료
                                        </span>

                                        <form method="post"
                                              action="${pageContext.request.contextPath}/reservation/cancel">

                                            <input type="hidden"
                                                   name="reservationId"
                                                   value="${reservation.reservationId}">

                                            <button type="submit"
                                                    class="button button-cancel">
                                                예약 취소
                                            </button>
                                        </form>
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