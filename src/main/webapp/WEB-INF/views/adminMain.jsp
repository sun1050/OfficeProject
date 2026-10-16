<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
    <title>관리자 | Office Mate</title>
    
    <!-- 모든 페이지 적용 기본 스타일 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/common.css">

    <!--헤더, 메뉴 등 레이아웃-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/layout.css">
    
    <!-- 관리자 대시보드·관리 목록 화면 전용 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/admin.css">
    
</head>
<body>
	<header class="header">
        <div class="header-inner">
            <a class="logo" href="${pageContext.request.contextPath}/main">
                Office Mate
            </a>

            <nav class="menu">
                <span class="menu-member">
                    <c:out value="${sessionScope.loginMemberName}" />님
                </span>

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

    <main class="container">
        <div class="page-heading">
            <p class="page-eyebrow">ADMIN</p>
            <h1>운영 관리</h1>
            <p>공유오피스 공간과 예약 현황을 관리합니다.</p>
        </div>

        <section class="admin-dashboard-grid">

            <a class="admin-dashboard-card"
               href="${pageContext.request.contextPath}/admin/reservation">

                <span class="admin-card-label">RESERVATION</span>
                <h2>전체 예약 관리</h2>
                <p>
                    이용일별 전체 예약 현황을 조회하고,
                    필요한 예약을 취소 처리합니다.
                </p>
                <span class="admin-card-link">예약 현황 보기 →</span>
            </a>

            <a class="admin-dashboard-card"
               href="${pageContext.request.contextPath}/admin/room">

                <span class="admin-card-label">SPACE</span>
                <h2>공간 관리</h2>
                <p>
                    전체 공간의 운영 상태를 확인하고,
                    점검 중 또는 운영 중 상태로 변경합니다.
                </p>
                <span class="admin-card-link">공간 목록 보기 →</span>
            </a>
            
            <a class="admin-dashboard-card"
               href="${pageContext.request.contextPath}/admin/statistics">

                <span class="admin-card-label">STATISTICS</span>
                <h2>이용 통계</h2>
                <p>
                    시작일과 종료일을 선택하여 날짜별 예약 건수와
                    공간 유형별 예약 현황을 그래프로 확인합니다.
                </p>
                <span class="admin-card-link">통계 보기 →</span>
            </a>

        </section>
    </main>

    <footer class="footer">
        © 2026 Office Mate. Shared workspace reservation service.
    </footer>
</body>
</html>