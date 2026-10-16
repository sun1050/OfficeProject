<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	
	<title>공간 관리 | Office Mate</title>
	
	
	<!--모든 페이지 적용 기본 스타일-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/common.css">

    <!--헤더, 메뉴 등 레이아웃-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/layout.css">

    <!--메인 화면에서만 사용-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/admin.css">
</head>
<body>
	<%-- 다른 JSP와 같은 공통 헤더 클래스(header, logo, menu)를 사용한다. --%>
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

    <main class="page-content admin-room-page">

        <div class="page-heading">
            <p class="page-eyebrow">ADMIN</p>
            <h1>공간 관리</h1>
            <p>각 공간의 운영 상태를 변경하고 예약 가능 여부를 관리합니다.</p>
        </div>

        <%-- AdminRoomListServlet이 세션에서 꺼내 전달한 1회성 처리 결과 메시지 --%>
        <c:if test="${not empty flashMessage}">
            <p class="form-success">
                <c:out value="${flashMessage}" />
            </p>
        </c:if>

        <section class="admin-list-section">
            <div class="admin-list-header">
                <h2>공간 목록</h2>
                <p>
                    총 <strong><c:out value="${rooms.size()}" /></strong>개
                </p>
            </div>

            <div class="table-scroll">
                <table class="reservation-table">
                    <thead>
                        <tr>
                            <th>번호</th>
                            <th>사진</th>
                            <th>공간명</th>
                            <th>공간 유형</th>
                            <th>정원</th>
                            <th>층</th>
                            <th>현재 상태</th>
                            <th>상태 변경</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach var="room" items="${rooms}">
                            <tr>
                                <td>
                                    <c:out value="${room.roomId}" />
                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${not empty room.imagePath}">
                                            <img class="admin-room-image"
                                                 src="${pageContext.request.contextPath}<c:out value='${room.imagePath}' />"
                                                 alt="<c:out value='${room.roomName}' /> 사진">
                                        </c:when>

                                        <c:otherwise>
                                            <span class="image-empty">사진 없음</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <c:out value="${room.roomName}" />
                                </td>

                                <td>
                                    <c:out value="${room.typeName}" />
                                </td>

                                <td>
                                    <c:out value="${room.maxCapacity}" />명
                                </td>

                                <td>
                                    <c:out value="${room.floorNo}" />층
                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${room.status eq 'AVAILABLE'}">
                                            <span class="status-badge status-active">
                                                운영 중
                                            </span>
                                        </c:when>

                                        <c:otherwise>
                                            <span class="status-badge status-canceled">
                                                점검 중
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <%--
                                        다음 단계에서 만들 AdminRoomStatusServlet이
                                        POST /admin/room/status 요청을 처리한다.
                                    --%>
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/admin/room/status"
                                          class="admin-room-status-form">

                                        <input type="hidden"
                                               name="roomId"
                                               value="${room.roomId}">

                                        <select name="status"
                                                aria-label="<c:out value='${room.roomName}' /> 상태 선택">
                                            <option value="AVAILABLE"
                                                <c:if test="${room.status eq 'AVAILABLE'}">selected</c:if>>
                                                운영 중
                                            </option>

                                            <option value="OUT_OF_SERVICE"
                                                <c:if test="${room.status eq 'OUT_OF_SERVICE'}">selected</c:if>>
                                                점검 중
                                            </option>
                                        </select>

                                        <button type="submit"
                                                class="button button-primary button-small">
                                            변경
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </section>
    </main>
</body>
</html>