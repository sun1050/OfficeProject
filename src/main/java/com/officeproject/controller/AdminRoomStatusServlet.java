package com.officeproject.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

import com.officeproject.room.RoomDAO;

//관리자가 공간의 운영 상태 변경  OUT_OF_SERVICE

@WebServlet("/admin/room/status")
public class AdminRoomStatusServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private final RoomDAO roomDAO = new RoomDAO();

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	HttpSession session = request.getSession(false);

        //예외적인 상황에서는 로그인 화면으로 돌려보냄
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String roomIdParam = request.getParameter("roomId");
        String status = request.getParameter("status");

        int roomId;

        try {
            roomId = Integer.parseInt(roomIdParam);

        } catch (NumberFormatException e) {
            session.setAttribute("flashMessage", "잘못된 공간 번호입니다.");

            response.sendRedirect(request.getContextPath() + "/admin/room");
            return;
        }

        /*
         * select 태그 값이라도 브라우저 개발자 도구로 바꿀 수 있으므로,
         * 서버에서 허용 상태인지 다시 확인한다.
         */
        if (!"AVAILABLE".equals(status)
                && !"OUT_OF_SERVICE".equals(status)) {

            session.setAttribute("flashMessage","변경할 수 없는 공간 상태입니다.");

            response.sendRedirect(request.getContextPath() + "/admin/room");
            return;
        }

        try {
            boolean updated = roomDAO.updateRoomStatus(roomId, status);

            if (updated) {
                session.setAttribute("flashMessage","공간 운영 상태를 변경했습니다.");

            } else {
                session.setAttribute("flashMessage","공간 상태를 변경하지 못했습니다.");
            }

            //새로고침해도 UPDATE 요청이 다시 전송되지 않는 PRG 패턴이다.
            response.sendRedirect(request.getContextPath() + "/admin/room");

        } catch (SQLException e) {
            throw new ServletException(
                    "공간 상태 변경 중 오류가 발생했습니다.",
                    e
            );
        }
	}

}
