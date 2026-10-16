package com.officeproject.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//
public class DBConnection {
	
	//Java 문자열 안에서 \ 하나를 표현하려면 \\로 작성
    //Tomcat 실행 환경에 옮김
    private static final String URL =
            System.getenv("OFFICE_DB_URL");

    private static final String USER =
            System.getenv("OFFICE_DB_USER");

    private static final String PASSWORD =
            System.getenv("OFFICE_DB_PASSWORD");

    //private 생성자> DBConnection 객체 여러 개 만들 필요 없으니까 new DBConnection() 막기
    private DBConnection() {
    }

    //DAO에 연결
    public static Connection getConnection() throws SQLException {

        try {
        	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        } catch (ClassNotFoundException e) {
            //JDBC .jar WEB-INF/lib에 잘 있는지, 프로젝트 Refresh 됐는지 확인
            throw new SQLException("SQL Server JDBC 드라이버를 찾을 수 없습니다.", e);
        }
        
        //성공 시 Connection 객체 반환
        //return DriverManager.getConnection(URL, USER, PASSWORD);
        if (URL == null || URL.isBlank()
                || USER == null || USER.isBlank()
                || PASSWORD == null || PASSWORD.isBlank()) {

            throw new SQLException(
                    "DB 환경변수가 설정되지 않았습니다. "
                    + "OFFICE_DB_URL, OFFICE_DB_USER, "
                    + "OFFICE_DB_PASSWORD를 확인해 주세요."
            );
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
