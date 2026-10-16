<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<!--로그인-->
	
	<title>로그인 | Office Mate</title>
	
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
            <h1>로그인</h1>
            <p class="auth-description">
                예약 서비스를 이용하려면 로그인해 주세요.
            </p>
            
            <c:if test="${not empty successMessage}">
			    <p class="form-success">
			        <c:out value="${successMessage}" />
			    </p>
			</c:if>
            
            <%-- LoginServlet이 전달한 오류 메시지 표시 --%>
            <c:if test="${not empty errorMessage}">
                <p class="form-error">
                    <c:out value="${errorMessage}" />
                </p>
            </c:if>

            <%-- type="submit"을 누르면 POST /login으로 요청된다. --%>
            <form method="post"
                  action="${pageContext.request.contextPath}/login">

                <div class="form-group">
                    <label for="loginId">아이디</label>
                    <input type="text"
                           id="loginId"
                           name="loginId"
                           value="<c:out value='${loginId}'/>"
                           placeholder="아이디를 입력하세요"
                           maxlength="30"
                           autocomplete="username"
                           required>
                </div>

                <div class="form-group">
                    <label for="password">비밀번호</label>
                    <input type="password"
                           id="password"
                           name="password"
                           placeholder="비밀번호를 입력하세요"
                           autocomplete="current-password"
                           required>
                </div>

                <button type="submit" class="button button-primary auth-submit">
                    로그인
                </button>
            </form>

            <p class="auth-link">
                아직 회원이 아니신가요?
                <a href="${pageContext.request.contextPath}/join">회원가입</a>
            </p>
        </section>
    </main>

    <footer class="footer">
        © 2026 Office Mate. Shared workspace reservation service.
    </footer>

</body>
</html>