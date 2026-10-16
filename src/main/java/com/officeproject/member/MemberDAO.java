package com.officeproject.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.officeproject.util.DBConnection;

public class MemberDAO {
	
	//로그인용 회원 조회
	public Member findByLoginId(String loginId) throws SQLException{
		
		//로그인 아이디로 회원 조회
		String sql =
				"SELECT member_id, login_id, password_hash, member_name, role "
				+ "FROM dbo.member "
				+ "WHERE login_id = ?";
		
		try(
			Connection conn = DBConnection.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql)
		){
            pstmt.setString(1, loginId);

            try (ResultSet rs = pstmt.executeQuery()) {
            	
            	//조회해서 찾으면 Member 객체로 돌려줌
                if (rs.next()) {
                	
                    return new Member(
                        rs.getInt("member_id"),
                        rs.getString("login_id"),
                        rs.getString("password_hash"),
                        rs.getString("member_name"),
                        rs.getString("role")
                    );
                }
            }
		}//try-with-resources 끝
		
		return null;
		
	}
	
	//아이디 중복 여부만. 이미 있으면 1 출력
	//실제 동시 가입 상황의 최종 방어는 DB의 UNIQUE(login_id) 제약이 담당
	public boolean existsByLoginId(String loginId) throws SQLException {
		
		
	    String sql =
	    		"SELECT 1 "
	    		+ "FROM dbo.member "
	    		+ "WHERE login_id = ?";

	    try (
	        Connection conn = DBConnection.getConnection();
	        PreparedStatement pstmt = conn.prepareStatement(sql)
	    ) {
	        pstmt.setString(1, loginId);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            return rs.next();
	        }
	    }
	}

	
	//회원가입
	//같은 패키지의 JoinResult.java(enum)
	public JoinResult createMember(
			
	        String loginId,
	        String passwordHash,
	        String memberName
	        
	) throws SQLException {

	    String sql =
	    		"INSERT INTO dbo.member ("
	    		+ "    login_id, "
	    		+ "    password_hash, "
	    		+ "    member_name "
	    		+ ") "
	    		+ "VALUES (?, ?, ?)";

	    try (
	        Connection conn = DBConnection.getConnection();
	        PreparedStatement pstmt = conn.prepareStatement(sql)
	    ) {
	        pstmt.setString(1, loginId);
	        pstmt.setString(2, passwordHash);
	        pstmt.setString(3, memberName);

	        int result = pstmt.executeUpdate();  //(영향받은 행의 개수)
	        
	        return result == 1
	                ? JoinResult.SUCCESS
	                : JoinResult.ERROR;

	    } catch (SQLException e) {

	        //2601: UNIQUE INDEX 중복
	        //2627: PRIMARY KEY 또는 UNIQUE 제약조건 위반
	        //화면에서 중복이 아니라고 확인했더라도 거의 동시에 같은 ID로 가입하면 여기서 걸리게
	        if (e.getErrorCode() == 2601 || e.getErrorCode() == 2627) {
	            return JoinResult.DUPLICATE_LOGIN_ID;  //
	        }

	        throw e;
	    }
	}

}
