package com.officeproject.util;

import org.mindrot.jbcrypt.BCrypt;

//최초 관리자 계정의 BCrypt 해시를 한 번 생성하기 위한 임시 클래스
//관리자 계정 생성 끝나고 삭제
public class PwHashGenerator {
	public static void main(String[] args) {
		String rawPassword = "비밀번호 입력";

        //work factor 12: 보안 높이는 BCrypt 반복 비용 설정
        String passwordHash = BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));

        System.out.println(passwordHash);
	}

}
