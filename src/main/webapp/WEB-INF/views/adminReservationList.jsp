<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
	<title>전체 예약 관리 | Office Mate</title>
	
	<!-- 모든 페이지 적용 기본 스타일 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/common.css">

    <!--헤더, 메뉴 등 레이아웃-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/layout.css">
    
    <!--관리자 화면 전용-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/admin.css">
	
</head>
<body>

	<header class="header">
        <div class="header-inner">
            <a class="logo"
               href="${pageContext.request.contextPath}/main">
                Office Mate
            </a>

            <nav class="menu">
                <a href="${pageContext.request.contextPath}/main">홈</a>
                <a href="${pageContext.request.contextPath}/rooms">공간 예약</a>
                <a href="${pageContext.request.contextPath}/reservation/my">내 예약</a>
                <a href="${pageContext.request.contextPath}/admin"
                   class="is-active">
                    관리자
                </a>
                <a href="${pageContext.request.contextPath}/logout">로그아웃</a>
            </nav>
        </div>
    </header>

    <main class="page-content admin-reservation-page">

        <div class="page-heading">
            <p class="page-eyebrow">ADMIN</p>
            <h1>전체 예약 관리</h1>
            <p>이용일 기준으로 전체 예약 현황을 조회합니다.</p>
        </div>

        <%-- GET 방식 검색: URL에 ?useDate=2026-10-16 형태로 날짜가 전달된다. --%>
        <form method="get"
              action="${pageContext.request.contextPath}/admin/reservation"
              class="admin-search-form">

            <div class="form-field">
                <label for="useDate">이용일</label>
                <input type="date"
                       id="useDate"
                       name="useDate"
                       value="<c:out value='${useDate}' />">
            </div>

            <div class="search-button-group">
                <button type="submit" class="button button-primary">
                    검색
                </button>

                <a href="${pageContext.request.contextPath}/admin/reservation"
                   class="button button-secondary">
                    전체 보기
                </a>
            </div>
        </form>

        <c:if test="${not empty errorMessage}">
            <p class="form-error">
                <c:out value="${errorMessage}" />
            </p>
        </c:if>
        <c:if test="${not empty flashMessage}">
            <p class="form-success">
                <c:out value="${flashMessage}" />
            </p>
        </c:if>

        <section class="admin-list-section">
            <div class="admin-list-header">
                <h2>예약 목록</h2>
                <p>
                    총 <strong><c:out value="${reservations.size()}" /></strong>건
                </p>
            </div>

            <c:choose>
                <c:when test="${empty reservations}">
                    <div class="empty-state">
                        <p>조건에 맞는 예약이 없습니다.</p>
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="table-scroll">
                        <table class="reservation-table">
                            <thead>
                                <tr>
                                    <th>번호</th>
                                    <th>이용일</th>
                                    <th>공간</th>
                                    <th>유형</th>
                                    <th>층</th>
                                    <th>예약자</th>
                                    <th>아이디</th>
                                    <th>상태</th>
                                    <th>신청 시각</th>
                                    <th>관리</th>
                                </tr>
                            </thead>

                            <tbody>
                                <c:forEach var="reservation"
                                           items="${reservations}">
                                    <tr>
                                        <td>
                                            <c:out value="${reservation.reservationId}" />
                                        </td>

                                        <td>
                                            <c:out value="${reservation.useDate}" />
                                        </td>

                                        <td>
                                            <c:out value="${reservation.roomName}" />
                                        </td>

                                        <td>
                                            <c:out value="${reservation.typeName}" />
                                        </td>

                                        <td>
                                            <c:out value="${reservation.floorNo}" />층
                                        </td>

                                        <td>
                                            <c:out value="${reservation.memberName}" />
                                        </td>

                                        <td>
                                            <c:out value="${reservation.loginId}" />
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${reservation.canceled}">
                                                    <span class="status-badge status-canceled">
                                                        취소됨
                                                    </span>
                                                </c:when>

                                                <c:otherwise>
                                                    <span class="status-badge status-active">
                                                        예약 완료
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td>
                                            <c:out value="${reservation.createdAt}" />
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${reservation.canceled}">
                                                    <span class="admin-action-disabled">
                                                        처리 완료
                                                    </span>
                                                </c:when>

                                                <c:otherwise>
                                                    <form method="post"
                                                          action="${pageContext.request.contextPath}/admin/reservation/cancel"
                                                          class="admin-cancel-form">

                                                        <input type="hidden"
                                                               name="reservationId"
                                                               value="${reservation.reservationId}">

                                                        <button type="submit"
                                                                class="button button-danger"
                                                                onclick="return confirm('이 예약을 취소하시겠습니까?');">
                                                            예약 취소
                                                        </button>
                                                    </form>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </main>
</body>
</html>