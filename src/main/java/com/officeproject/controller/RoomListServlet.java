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

import com.officeproject.room.Room;
import com.officeproject.room.RoomDAO;

@WebServlet("/rooms")
public class RoomListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final RoomDAO roomDAO = new RoomDAO();
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		HttpSession session = request.getSession(false);

		//사용자의 예약 후 ReservationCreateServlet의 메시지 출력
		if (session != null) {
		    Object flashMessage = session.getAttribute("flashMessage");

		    if (flashMessage instanceof String) {
		    	//세션값 요청값으로 옮기고 세션값은 바로 지우기
		        request.setAttribute("flashMessage", flashMessage);
		        session.removeAttribute("flashMessage");
		    }
		}
		
		LocalDate today = LocalDate.now();
        LocalDate useDate = today;

        String useDateParameter = request.getParameter("useDate");
        
        

        if (useDateParameter != null && !useDateParameter.isBlank()) {
        	
            try {
                useDate = LocalDate.parse(useDateParameter);

                if (useDate.isBefore(today)) {
                    request.setAttribute(
                        "errorMessage",
                        "오늘 이전 날짜는 조회할 수 없습니다."
                    );

                    useDate = today;
                }

            } catch (DateTimeParseException e) {
                request.setAttribute(
                    "errorMessage",
                    "날짜 형식이 올바르지 않습니다."
                );
            }
        }

        try {
            List<Room> rooms = roomDAO.findAvailableRooms(useDate);

            request.setAttribute("rooms", rooms);
            request.setAttribute("useDate", useDate.toString());
            request.setAttribute("today", today.toString());

            request.getRequestDispatcher("/WEB-INF/views/roomList.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("공간 조회 중 DB 오류가 발생했습니다.", e);
        }
    }
	
}