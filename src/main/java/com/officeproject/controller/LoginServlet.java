package com.officeproject.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import com.officeproject.member.Member;
import com.officeproject.member.MemberDAO;

//login.jsp보여주기, 아이디,비밀번호 검증

//doGet: login.jsp보여주기.
//doPost: 아이디/비밀번호 검증, 세션 생성

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final MemberDAO memberDAO = new MemberDAO();
       
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//회원가입 안내
		if ("1".equals(request.getParameter("joined"))) {
		    request.setAttribute("successMessage", "회원가입이 완료되었습니다. 로그인해 주세요!");
		}
		//login.jsp로 화면 전달
		request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
	}

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String loginId = request.getParameter("loginId");
        String password = request.getParameter("password");

        //로그인 실패 시 입력했던 ID만 다시 보여줌, 비밀번호는X
        request.setAttribute("loginId", loginId);

        if (loginId==null || loginId.isBlank() || password==null || password.isBlank()){

            request.setAttribute("errorMessage", "아이디와 비밀번호를 모두 입력해 주세요.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        try {
            Member member = memberDAO.findByLoginId(loginId.trim());

            //DB의 BCrypt 해시와 사용자입력 평문 비밀번호 비교>BCrypt.checkpw()(salt 포함한 해시값)
            if (member==null || !BCrypt.checkpw(password, member.getPasswordHash())){
                request.setAttribute("errorMessage", "아이디 또는 비밀번호가 올바르지 않습니다.");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                return;
            }

            //로그인 성공 전의 기존 세션 폐기.세션 고정 공격(Session Fixation) 방어
            HttpSession oldSession = request.getSession(false);

            if (oldSession != null){
                oldSession.invalidate();
            }

            //로그인 성공 후 새 세션. 이후 권한 확인에 필요한 최소 정보만 저장
            HttpSession session=request.getSession(true);

            session.setAttribute("loginMemberId", member.getMemberId());
            session.setAttribute("loginMemberName", member.getMemberName());
            session.setAttribute("loginMemberRole", member.getRole());

            //30분 동안 요청 없으면 세션 만료. 초단위
            session.setMaxInactiveInterval(30*60);

            response.sendRedirect(request.getContextPath()+"/main");

        } catch (SQLException e){
            throw new ServletException("로그인 처리 중 데이터베이스 오류가 발생했습니다.", e);
        }
    }

}
