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

import com.officeproject.room.AdminRoom;
import com.officeproject.room.RoomDAO;

//관리자가 전체 공간 목록과 운영 상태 조회
// /admin/* > AdminAuthorizationFilter가 먼저 실행되어 ADMIN 권한이 있는 계정만 접근

@WebServlet("/admin/room")
public class AdminRoomListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private final RoomDAO roomDAO = new RoomDAO();

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	//상태 변경 Servlet이 세션에 넣은 1회성 메시지 꺼냄
        HttpSession session = request.getSession(false);

        if (session != null) {
            Object flashMessage = session.getAttribute("flashMessage");

            if (flashMessage instanceof String) {
                request.setAttribute("flashMessage", flashMessage);
                session.removeAttribute("flashMessage");
            }
        }

        try {
            List<AdminRoom> rooms = roomDAO.findAllForAdmin();

            //adminRoomList.jsp에서 ${rooms}로 사용할 값
            request.setAttribute("rooms", rooms);

            request.getRequestDispatcher("/WEB-INF/views/adminRoomList.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "관리자 공간 목록을 조회하는 중 오류가 발생했습니다.",
                    e
            );
        }
	}

}
