package com.officeproject.member;

public class Member {
	private final int memberId;
    private final String loginId;
    private final String passwordHash;
    private final String memberName;
    private final String role;
    
    public Member(int memberId, String loginId, String passwordHash, String memberName, String role) {
    	this.memberId = memberId;
    	this.loginId = loginId;
    	this.passwordHash = passwordHash;
    	this.memberName = memberName;
    	this.role = role;
    }
    
	public int getMemberId() {
		return memberId;
	}
	public String getLoginId() {
		return loginId;
	}
	public String getPasswordHash() {
		return passwordHash;
	}
	public String getMemberName() {
		return memberName;
	}
	public String getRole() {
		return role;
	}

    
    
}
