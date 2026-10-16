package com.officeproject.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.officeproject.util.DBConnection; //DBConnection사용하려면 필요

//Mssql이랑 잘 이어졌는지 테스트
@WebServlet("/test")
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 /*
         * ?에 전달할 값이 없어도 PreparedStatement를 사용한다.
         * 이후 실제 SQL에서도 같은 방식으로 작성할 것이다.
         */
        String sql = "SELECT DB_NAME() AS current_database, SUSER_SNAME() AS login_name";

        /*
         * try-with-resources:
         * 블록이 끝날 때 ResultSet, PreparedStatement, Connection을 자동으로 닫는다.
         * DB 연결을 반환하지 않으면 나중에 접속 가능한 연결이 고갈될 수 있다.
         */
        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {

            if (rs.next()) {
                request.setAttribute("currentDatabase", rs.getString("current_database"));
                request.setAttribute("loginName", rs.getString("login_name"));
                request.setAttribute("connectionSuccess", true);
            }

        } catch (SQLException e) {
            /*
             * 학습·로컬 점검 단계에서만 오류 내용을 화면에 전달한다.
             * 실제 외부 서비스에서는 DB 오류 전문을 사용자에게 노출하지 않는다.
             */
            request.setAttribute("connectionSuccess", false);
            request.setAttribute("errorMessage", e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/test.jsp")
               .forward(request, response);
    }

}
