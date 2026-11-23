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
    
	//main.jsp에서 사용 (<a href="${pageContext.request.contextPath}/join">)
	//사용자가 회원가입 화면 열 때 사용 > getRequestDispatcher로 join.jsp로 전달
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//서버 내부에서 join.jsp 실행
    	request.getRequestDispatcher("/WEB-INF/views/join.jsp").forward(request, response);
    	
    	//response.sendRedirect(request.getContextPath() + "/join");를 쓰면 /join에(@WebServlet("/join")) 다시 요청하라는 뜻.
    	/*response.sendRedirect(
    		    request.getContextPath() + "/WEB-INF/views/join.jsp"
    		);를 쓰면 브라우저가 그 파일을 직접 요청함. 하지만 WEB-INF는 브라우저의 직접 접근을 막아 둔 폴더라 화면을 열 수 없음. 서버 내부의 forward로는 사용 가능.
    		
    	*/
	}
    
    
    //join.jsp에서 사용 (<form method="post" action="${pageContext.request.contextPath}/join">)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	//브라우저가 보낸 입력값을 서버에서 UTF-8로 해석하도록 지정>한글 안깨지게
        request.setCharacterEncoding("UTF-8");

        //getParameter는 join.jsp의 name="loginId"과 맞춤.
        String loginId = request.getParameter("loginId"); //sun1234
        String memberName = request.getParameter("memberName");  //홍길동
        String password = request.getParameter("password");  //example123
        String passwordConfirm = request.getParameter("passwordConfirm");  //example123

        //가입 실패 시 JSP에서 아이디와 이름을 다시 표시하도록 request에 저장(forward 때 사용)
        request.setAttribute("loginId", loginId);
        request.setAttribute("memberName", memberName);

        //아이디 항목이 전송되지 않았는지(jsp name이 getParameter와 틀릴때) || ! 영문 대·소문자 또는 숫자 4~30자 /  ^, $	:문자열의 시작과 끝
        if (loginId==null || !loginId.matches("^[A-Za-z0-9]{4,30}$")) {
            forwardWithError(  //내가 만든 forwardWithError 메서드. 에러메세지로 저장하고 forward로 join.jsp로 이동
                request,
                response,
                "아이디는 영문과 숫자로 4~30자 입력해 주세요."
            );
            return;
        }

        //
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

        //
        if (!password.equals(passwordConfirm)) {
            forwardWithError(
                request,
                response,
                "비밀번호 확인이 일치하지 않습니다."
            );
            return;
        }
        
        //양옆 공백 제거
        String normalizedLoginId = loginId.trim();
        String normalizedMemberName = memberName.trim();

        try {
            //사용자에게 중복 안내 보여 주기용-최종 중복 방지는 SQL의 UNIQUE. 중복이면 1출력됨
        	//existsByLoginId: MemberDAO의 아이디 중복 확인
            if (memberDAO.existsByLoginId(normalizedLoginId)) {
                forwardWithError(request, response, "이미 사용 중인 아이디입니다.");
                return;
            }

            //BCrypt가 매번 새로운 salt 포함 해시를 만들게
            String passwordHash=BCrypt.hashpw(password, BCrypt.gensalt(12));
            
            //JoinResult.java enum 사용 SUCCESS, DUPLICATE_LOGIN_ID, ERROR
            //createMember: 회원가입 login_id, password_hash, member_name 인서트
            JoinResult joinResult=memberDAO.createMember(
                normalizedLoginId,
                passwordHash,
                normalizedMemberName
            );

            //LoginServlet(@WebServlet("/login"))으로 감. ?joined=1은 전달할 파라미터일 뿐이다.
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

            //여기부터 SUCCESS, DUPLICATE_LOGIN_ID를 제외한 ERROR
            forwardWithError(
                request,
                response,
                "회원가입 처리에 실패했습니다. 다시 시도해 주세요."
            );

        } catch (SQLException e) { //DAO가 결과(SUCCESS, DUPLICATE_LOGIN_ID, ERROR)를 반환하지 못하고 예외를 던지는 경우
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
