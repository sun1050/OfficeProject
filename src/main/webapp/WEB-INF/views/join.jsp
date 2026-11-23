<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<!--로그인-->
	
	<title>회원가입 | Office Mate</title>
	
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

            <nav class="menu">
                <a href="${pageContext.request.contextPath}/login">로그인</a>
            </nav>
        </div>
    </header>

    <main class="auth-page">
        <section class="auth-card">
            <h1>회원가입</h1>

            <p class="auth-description">
                Office Mate 이용을 위한 계정을 만들어 주세요.
            </p>
            
            <c:if test="${not empty errorMessage}">
                <p class="form-error">
                    <c:out value="${errorMessage}" />
                </p>
            </c:if>

			<!--
			method="post":	입력값을 HTTP 요청 본문에 담아서 전송 
			action="…/join": 폼을 제출할 서버 주소
			${pageContext.request.contextPath}:	앱의 배포 경로. /OfficeProject
			${pageContext.request.contextPath}/join > JoinServlet.java로 감(@WebServlet(/join)))
			-->
            <form method="post"
                  action="${pageContext.request.contextPath}/join">

				<!--
				id="loginId" : label for="loginId"과 맞춰짐. HTML에서 이 입력칸을 식별하는 이름
				name="loginId" : JoinServlet.java의 getParameter()에 사용. 입력값을 서버에 전송할 때 붙이는 이름
				value="<c:out value='${loginId}'/>" : JoinServlet.java의 setAttribute()에 사용. 받아오는 값
				-->
				<!--  -->
                <div class="form-group">
                    <label for="loginId">아이디</label>
                    <input type="text"
                           id="loginId"
                           name="loginId"
                           value="<c:out value='${loginId}'/>"
                           placeholder="영문·숫자 4~30자"
                           maxlength="30"
                           autocomplete="username"
                           required>
                </div>

                <div class="form-group">
                    <label for="memberName">이름</label>
                    <input type="text"
                           id="memberName"
                           name="memberName"
                           value="<c:out value='${memberName}'/>"
                           placeholder="이름을 입력하세요"
                           maxlength="30"
                           autocomplete="name"
                           required>
                </div>

                <div class="form-group">
                    <label for="password">비밀번호</label>
                    <input type="password"
                           id="password"
                           name="password"
                           placeholder="8자 이상 입력하세요"
                           minlength="8"
                           autocomplete="new-password"
                           required>
                </div>

                <div class="form-group">
                    <label for="passwordConfirm">비밀번호 확인</label>
                    <input type="password"
                           id="passwordConfirm"
                           name="passwordConfirm"
                           placeholder="비밀번호를 한 번 더 입력하세요"
                           minlength="8"
                           autocomplete="new-password"
                           required>
                </div>

                <button type="submit" class="button button-primary auth-submit">
                    회원가입
                </button>
            </form>

            <p class="auth-link">
                이미 계정이 있으신가요?
                <a href="${pageContext.request.contextPath}/login">로그인</a>
            </p>
        </section>
    </main>

    <footer class="footer">
        © 2026 Office Mate. Shared workspace reservation service.
    </footer>

</body>
</html>