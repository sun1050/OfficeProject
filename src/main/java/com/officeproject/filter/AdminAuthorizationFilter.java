package com.officeproject.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

// /admin/...주소 요청 다 가로채서 로그인 여부와 관리자 역할을 확인하는 Filter
// 앞으로 /admin/reservations 같은 Servlet 추가해도 이 Filter가 공통으로 권한 검사

@WebFilter(urlPatterns= {"/admin", "/admin/*"})
public class AdminAuthorizationFilter extends HttpFilter implements Filter {
       
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		//System.out.println("AdminAuthorizationFilter 돌아감");
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        HttpSession session = httpRequest.getSession(false);

        //로그인하지 않은 경우 로그인 화면으로
        if (session==null || session.getAttribute("loginMemberId")==null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        //일반 회원이 관리자 URL 접근
        Object loginMemberRole = session.getAttribute("loginMemberRole");

        if (!"ADMIN".equals(loginMemberRole)) {
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);

            httpRequest.getRequestDispatcher("/WEB-INF/views/accessDenied.jsp")
                       .forward(httpRequest, httpResponse);
            return;
        }

        //로그인한 관리자만 실제 관리자 Servlet으로 통과
		chain.doFilter(request, response);
	}


}
