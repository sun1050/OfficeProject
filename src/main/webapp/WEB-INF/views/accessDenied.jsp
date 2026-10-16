<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>접근 권한 없음 | Office Mate</title>
	
	<!-- 모든 페이지 적용 기본 스타일 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/common.css">

    <!--헤더, 메뉴 등 레이아웃-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/layout.css">

    <!--로그인/회원가입만 사용-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/auth.css">
    
</head>
<body>
	<header class="header">
        <div class="header-inner">
            <a class="logo" href="${pageContext.request.contextPath}/main">
                Office Mate
            </a>
        </div>
    </header>

    <main class="auth-page">
        <section class="auth-card">
            <h1>접근 권한이 없습니다.</h1>

            <p class="auth-description">
                이 메뉴는 관리자만 이용할 수 있습니다.
            </p>

            <a class="button button-primary auth-submit"
               href="${pageContext.request.contextPath}/main">
                메인으로 돌아가기
            </a>
        </section>
    </main>

    <footer class="footer">
        © 2026 Office Mate. Shared workspace reservation service.
    </footer>
</body>
</html>