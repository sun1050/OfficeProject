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

// /reservation/... 요청은 로그인한 회원만 접근 가능

@WebFilter("/reservation/*")
public class LoginAuthorizationFilter extends HttpFilter implements Filter {
       
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);

        if (session==null || session.getAttribute("loginMemberId")==null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }
		chain.doFilter(request, response);
	}

}
