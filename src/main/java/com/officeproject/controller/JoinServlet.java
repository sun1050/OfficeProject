package com.officeproject.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import com.officeproject.member.JoinResult;
import com.officeproject.member.MemberDAO;

//join.jsp보여주기, 아이디 중복 확인, BCrypt 암호화, DAO호출
//doGet: jogin.jsp보여주기.
//doPost: 아이디 중복 확인, BCrypt 암호화, DAO호출

@WebServlet("/join")
public class JoinServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final MemberDAO memberDAO = new MemberDAO();
       
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//join.jsp로 화면 전달
    	request.getRequestDispatcher("/WEB-INF/views/join.jsp").forward(request, response);
	}
    
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String loginId = request.getParameter("loginId");
        String memberName = request.getParameter("memberName");
        String password = request.getParameter("password");
        String passwordConfirm = request.getParameter("passwordConfirm");

        //실패 시 아이디와 이름만 다시 화면에 전달
        request.setAttribute("loginId", loginId);
        request.setAttribute("memberName", memberName);

        if (loginId==null || !loginId.matches("^[A-Za-z0-9]{4,30}$")) {
            forwardWithError(
                request,
                response,
                "아이디는 영문과 숫자로 4~30자 입력해 주세요."
            );
            return;
        }

        if (memberName==null || memberName.isBlank() || memberName.trim().length() > 30) {

            forwardWithError(
                request,
                response,
                "이름은 1~30자로 입력해 주세요."
            );
            return;
        }

        if (password == null || password.length() < 8) {
            forwardWithError(
                request,
                response,
                "비밀번호는 8자 이상 입력해 주세요."
            );
            return;
        }

        if (!password.equals(passwordConfirm)) {
            forwardWithError(
                request,
                response,
                "비밀번호 확인이 일치하지 않습니다."
            );
            return;
        }
        
        String normalizedLoginId = loginId.trim();
        String normalizedMemberName = memberName.trim();

        try {
            //사용자에게 중복 안내 보여 주기용-최종 중복 방지는 SQL의 UNIQUE
        	//existsByLoginId: MemberDAO의 아이디 중복 확인
            if (memberDAO.existsByLoginId(normalizedLoginId)) {
                forwardWithError(request, response, "이미 사용 중인 아이디입니다.");
                return;
            }

            //BCrypt가 매번 새로운 salt 포함 해시를 만들게
            String passwordHash=BCrypt.hashpw(password, BCrypt.gensalt(12));
            
            //enum
            JoinResult joinResult=memberDAO.createMember(
                normalizedLoginId,
                passwordHash,
                normalizedMemberName
            );

            if (joinResult==JoinResult.SUCCESS) {
                response.sendRedirect(
                    request.getContextPath()+"/login?joined=1"
                );
                return;
            }

            if (joinResult==JoinResult.DUPLICATE_LOGIN_ID) {
                forwardWithError(
                    request,
                    response,
                    "이미 사용 중인 아이디입니다."
                );
                return;
            }

            forwardWithError(
                request,
                response,
                "회원가입 처리에 실패했습니다. 다시 시도해 주세요."
            );

        } catch (SQLException e) {
            throw new ServletException("회원가입 처리 중 DB 오류가 발생했습니다.", e);
        }
    }

    //가입 실패 화면을 같은 요청 안에서 다시 보여 주는 공통 메서드.
    private void forwardWithError(
            HttpServletRequest request,
            HttpServletResponse response,
            String errorMessage
    ) throws ServletException, IOException {

        request.setAttribute("errorMessage", errorMessage);

        request.getRequestDispatcher("/WEB-INF/views/join.jsp").forward(request, response);
    }


}
