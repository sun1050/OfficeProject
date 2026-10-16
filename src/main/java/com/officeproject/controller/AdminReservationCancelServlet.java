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

//관리자가 전체 예약 관리 화면에서 예약을 취소할 때 실행
//AdminAuthorizationFilter가 ADMIN 권한을 먼저 검사

@WebServlet("/admin/reservation/cancel")
public class AdminReservationCancelServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private final ReservationDAO reservationDAO = new ReservationDAO();
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);

        //관리자 필터를 이미 통과했으므로 세션은 원칙적으로 존재.
        //그래도 예외 상황을 대비해 로그인 화면으로 보낸다.
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String reservationIdParam = request.getParameter("reservationId");

        int reservationId;

        try {
            reservationId = Integer.parseInt(reservationIdParam);

        } catch (NumberFormatException e) {
            session.setAttribute("flashMessage","잘못된 예약 번호입니다.");

            response.sendRedirect(request.getContextPath() + "/admin/reservation");
            return;
        }

        try {
            boolean canceled =
                    reservationDAO.cancelReservationByAdmin(reservationId);

            if (canceled) {
                session.setAttribute("flashMessage","예약을 취소했습니다.");

            } else {
                session.setAttribute("flashMessage","이미 취소되었거나 존재하지 않는 예약입니다.");
            }

            //POST 처리 후 목록 페이지로 이동
            //새로고침해도 취소 POST가 다시 전송되지 않는 PRG 패턴
            response.sendRedirect(request.getContextPath() + "/admin/reservation");

        } catch (SQLException e) {
            throw new ServletException(
                    "관리자 예약 취소 중 오류가 발생했습니다.",
                    e
            );
        }
	}

}
