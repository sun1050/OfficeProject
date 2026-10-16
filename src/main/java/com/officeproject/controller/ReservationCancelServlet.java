package com.officeproject.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

import com.officeproject.reservation.ReservationDAO;

//로그인한 회원 본인의 예약만 취소한다.

@WebServlet("/reservation/cancel")
public class ReservationCancelServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private final ReservationDAO reservationDAO = new ReservationDAO();
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		HttpSession session = request.getSession(false);

        int memberId = (Integer) session.getAttribute("loginMemberId");

        String reservationIdParameter =request.getParameter("reservationId");

        try {
            int reservationId=Integer.parseInt(reservationIdParameter);

            if (reservationId<=0) {
                throw new IllegalArgumentException();
            }

            boolean canceled=reservationDAO.cancelReservation(reservationId, memberId);

            if (canceled) {
                session.setAttribute("flashMessage","예약이 취소되었습니다.");
            } else {
                session.setAttribute("flashMessage","취소할 수 없거나 이미 취소된 예약입니다.");
            }

        } catch (IllegalArgumentException e) {
            session.setAttribute("flashMessage","잘못된 예약 요청입니다.");

        } catch (SQLException e) {
            throw new ServletException(
                "예약 취소 중 DB 오류가 발생했습니다.",
                e
            );
        }

        response.sendRedirect(request.getContextPath() + "/reservation/my");
    }

}
