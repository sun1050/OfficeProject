package com.officeproject.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.officeproject.reservation.ReservationCreateResult;
import com.officeproject.reservation.ReservationDAO;

//선택한 공간과 날짜로 예약 생성

@WebServlet("/reservation/create")
public class ReservationCreateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private final ReservationDAO reservationDAO = new ReservationDAO();
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		HttpSession session = request.getSession(false);

        int memberId = (Integer) session.getAttribute("loginMemberId");

        String roomIdParameter = request.getParameter("roomId");
        String useDateParameter = request.getParameter("useDate");

        
        int roomId;
        LocalDate useDate;

        try {
            roomId = Integer.parseInt(roomIdParameter);
            useDate = LocalDate.parse(useDateParameter);

            if (roomId <= 0 || useDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException();
            }

        } catch (IllegalArgumentException | DateTimeParseException e) {
            session.setAttribute("flashMessage", "예약 요청 값이 올바르지 않습니다.");

            response.sendRedirect(request.getContextPath() + "/rooms");
            return;
        }

        try {
        	//예약하는 reservationDAO로 예약생성
            ReservationCreateResult result=reservationDAO.createReservation(memberId, roomId, useDate);

            if (result == ReservationCreateResult.SUCCESS) {
            	
                session.setAttribute("flashMessage", "공간 예약이 완료되었습니다.");

            } else if (result == ReservationCreateResult.ALREADY_RESERVED) {
            	
                session.setAttribute("flashMessage", "다른 사용자가 먼저 예약한 공간입니다.");

            } else if (result == ReservationCreateResult.ROOM_NOT_AVAILABLE) {
                
            	session.setAttribute("flashMessage", "현재 예약할 수 없는 공간입니다.");

            } else {
                session.setAttribute("flashMessage", "예약 처리에 실패했습니다. 다시 시도해 주세요.");
            }
            
            //POST 후 새로고침 시 중복 INSERT가 일어나지 않도록 PRG(Post-Redirect-Get) 패턴을 사용
            response.sendRedirect(
                request.getContextPath() + "/rooms?useDate=" + useDate
            );

        } catch (SQLException e) {
            throw new ServletException("예약 처리 중 DB 오류가 발생했습니다.", e);
        }
	}

}
