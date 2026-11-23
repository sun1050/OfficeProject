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
	
	//main.jsp의 ${pageContext.request.contextPath}/rooms
	//roomList.jsp ${pageContext.request.contextPath}/rooms}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);

		//사용자의 예약 후 ReservationCreateServlet의 메시지 출력
		//세션(로그인) 했나 확인
		if (session != null) {
			//ReservationCreateServlet.java의 session.setAttribute("flashMessage", "공간 예약이 완료되었습니다.");
			//ReservationCreateServlet에서 예약 성공 후 /rooms로 redirect해서 왔냐?("/rooms?useDate=" + useDate)
			//
		    Object flashMessage = session.getAttribute("flashMessage");

		    if (flashMessage instanceof String) {  //flashMessage가 문자열이면 true. 숫자나 비어있으면 false
		    	//세션값 요청값으로 옮기고 세션값은 바로 지우기
		        request.setAttribute("flashMessage", flashMessage);
		        session.removeAttribute("flashMessage");
		    }
		}
		
		LocalDate today = LocalDate.now();  //지금 날짜
        LocalDate useDate = today;  //실제로 공간을 조회할 이용 날짜를 일단 지금 날짜로 하겠다.

        //roomList.jsp의 name="useDate"
        String useDateParameter = request.getParameter("useDate");
        
        //roomList.jsp의 name 값 getAttribute와 일치함+useDateParameter가 파라미터에 있어야 함 && 비어있지 않음
        //>>사용자가 날짜를 직접 지정함. main.jsp에서 들어오는 사용자는 useDateParameter = null이여서 이 if문 건너 뜀
        if (useDateParameter != null && !useDateParameter.isBlank()) {
        	
            try {//>>사용자가 날짜를 직접 지정함. /rooms?useDate=2026-10-25
            	//파라미터는 String 밖에 안되니까 LocalDate로 변환  /  "2026-10-25" > 2026-10-25
            	//날짜 비교와 DB 조회에 사용 목적
                useDate = LocalDate.parse(useDateParameter);

                //오늘 이전 날짜일 때 > 오늘 날짜로 수정
                if (useDate.isBefore(today)) {
                    request.setAttribute(
                        "errorMessage",
                        "오늘 이전 날짜는 조회할 수 없습니다."
                    );

                    useDate = today;
                }

            } catch (DateTimeParseException e) {  //rooms?useDate=abc
                request.setAttribute(
                    "errorMessage",
                    "날짜 형식이 올바르지 않습니다."
                );
            }
        }

        try {
        	//roomDAO의 findAvailableRooms(선택한 날짜에 예약 가능한 공간 조회) > 
            List<Room> rooms = roomDAO.findAvailableRooms(useDate);

            //rooms에 있는 요소: Room.java 객체의 roomId, roomName, imagePath, floorNo, typeName, maxCapacity
            request.setAttribute("rooms", rooms); //rooms 목록 전체를 보냄(findAvailableRooms 결과 객체들)
            request.setAttribute("useDate", useDate.toString());
            request.setAttribute("today", today.toString());

            request.getRequestDispatcher("/WEB-INF/views/roomList.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("공간 조회 중 DB 오류가 발생했습니다.", e);
        }
    }
	
}