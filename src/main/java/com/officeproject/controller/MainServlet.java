package com.officeproject.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

//브라우저가 /main 주소로 GET 요청하면 실행

@WebServlet("/main")
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /*public MainServlet() {
        super();
        // TODO Auto-generated constructor stub
    }*/

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//데이터를 jsp로 전달
		request.setAttribute("pageTitle", "공유오피스 좌석 예약");
		
		//서버 내부에서 JSP로 이동, 위에서 저장한 pageTitle 값 유지(요청 안끝나서)
		request.getRequestDispatcher("WEB-INF/views/main.jsp").forward(request, response);
		
	}

}
