package com.officeproject.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.officeproject.reservation.MyReservation;
import com.officeproject.reservation.ReservationDAO;

//로그인한 회원 자신의 예약 목록만 보여 줌. LoginAuthorizationFilter가 먼저 로그인 여부를 검사

@WebServlet("/reservation/my")
public class MyReservationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private final ReservationDAO reservationDAO = new ReservationDAO();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		HttpSession session = request.getSession(false);

        int memberId = (Integer) session.getAttribute("loginMemberId");

        //예약 취소 후 Redirect되어 온 메시지 보여줌
        Object flashMessage = session.getAttribute("flashMessage");

        if (flashMessage instanceof String) {
            request.setAttribute("flashMessage", flashMessage);
            session.removeAttribute("flashMessage");
        }

        try {
        	
        	//MyReservation.java - JavaBean객체
            List<MyReservation> reservations =reservationDAO.findByMemberId(memberId);

            request.setAttribute("reservations", reservations);

            request.getRequestDispatcher("/WEB-INF/views/myReservationList.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                "내 예약 목록 조회 중 DB 오류가 발생했습니다.",
                e
            );
        }
	}


}
