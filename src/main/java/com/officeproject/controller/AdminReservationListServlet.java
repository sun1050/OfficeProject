package com.officeproject.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.officeproject.reservation.AdminReservation;
import com.officeproject.reservation.ReservationDAO;

//관리자 전체 예약 조회 화면 담당
// /admin/* 주소이므로 AdminAuthorizationFilter가 먼저 실행되어 관리자(ADMIN)만 이 Servlet에 접근 가능
@WebServlet("/admin/reservation")
public class AdminReservationListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private final ReservationDAO reservationDAO = new ReservationDAO();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//취소 Servlet이 세션에 저장한 1회성 메시지를 꺼냄
        HttpSession session = request.getSession(false);

        if (session != null) {
            Object flashMessage = session.getAttribute("flashMessage");

            if (flashMessage instanceof String) {
                request.setAttribute("flashMessage", flashMessage);
                session.removeAttribute("flashMessage");  //JSP로 전달한 즉시 세션에서는 제거
            }
        }
		//검색창의 useDate 값을 받는데 아무 날짜도 입력하지 않으면 null > DAO는 전체 예약을 조회
       String useDateParam = request.getParameter("useDate");
       LocalDate useDate = null;

       if (useDateParam != null && !useDateParam.isBlank()) {
           try {
               useDate = LocalDate.parse(useDateParam);

           } catch (DateTimeParseException e) {
               //사용자가 주소의 날짜 값을 직접 이상하게 바꾼 경우도 대비(브라우저 주소창 조작)
               request.setAttribute(
                       "errorMessage",
                       "날짜 형식이 올바르지 않습니다."
               );
           }
       }

       try {
    	   //AdminReservation.java: 관리자가 전체 예약 목록을 조회할 때 사용하는 데이터 객체(DTO)
           List<AdminReservation> reservations=reservationDAO.findForAdmin(useDate);

           //JSP의 ${reservations}와 ${useDate}에서 사용할 값
           request.setAttribute("reservations", reservations);
           request.setAttribute("useDate", useDateParam);

           request.getRequestDispatcher(
                   "/WEB-INF/views/adminReservationList.jsp"
           ).forward(request, response);

       } catch (SQLException e) {
           throw new ServletException(
                   "관리자 예약 목록을 조회하는 중 오류가 발생했습니다.",
                   e
           );
       }
		
		
	}

}
