package com.officeproject.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/admin")
public class AdminMainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	//관리자 화면
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	/* >>권한 검사는 AdminAuthorizationFilter가 담당
    	//단순 권한 확인때는 새 세션 필요 없음
    	HttpSession session = request.getSession(false);
    	
    	//loginMemberId: LoginServlet.java에서 로그인 후 새로 받은 세션
    	//로그인 안했을 때
    	if (session==null || session.getAttribute("loginMemberId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
    	
        Object loginMemberRole = session.getAttribute("loginMemberRole");
        
        //role 값이 null이어도 NullPointerException 없이 false 됨("ADMIN"을 앞에 써서)
        if (!"ADMIN".equals(loginMemberRole)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);  //브라우저에 권한없음 HTTP 상태 설정, SC_FORBIDDEN: 403(로그인 했지만 권한 없음)
            
            request.getRequestDispatcher("/WEB-INF/views/accessDenied.jsp").forward(request, response);
            
            return;
        }
        */
    	
        //관리자 화면을 보여 주는 일만 담당
        request.getRequestDispatcher("/WEB-INF/views/adminMain.jsp")
               .forward(request, response);
	}

}
