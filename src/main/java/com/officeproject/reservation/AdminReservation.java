package com.officeproject.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AdminReservation {

	private final int reservationId;
    private final String memberName;
    private final String loginId;
    private final String roomName;
    private final String typeName;
    private final short floorNo;
    private final LocalDate useDate;
    private final boolean canceled;
    private final LocalDateTime createdAt;
    
    //관리자가 전체 예약 목록을 조회할 때 사용하는 데이터 객체(DTO). JavaBean은 테이블의 데이터 담는 객체
    //일반 사용자의 MyReservation과 달리 누가 예약했는지(memberName, loginId)까지 함께 담는다.
    
    public AdminReservation(int reservationId, String memberName, String loginId, String roomName, String typeName,
            short floorNo, LocalDate useDate, boolean canceled, LocalDateTime createdAt) {

        this.reservationId = reservationId;
        this.memberName = memberName;
        this.loginId = loginId;
        this.roomName = roomName;
        this.typeName = typeName;
        this.floorNo = floorNo;
        this.useDate = useDate;
        this.canceled = canceled;
        this.createdAt = createdAt;
    }
    
    
	public int getReservationId() {
		return reservationId;
	}
	public String getMemberName() {
		return memberName;
	}
	public String getLoginId() {
		return loginId;
	}
	public String getRoomName() {
		return roomName;
	}
	public String getTypeName() {
		return typeName;
	}
	public short getFloorNo() {
		return floorNo;
	}
	public LocalDate getUseDate() {
		return useDate;
	}
	public boolean isCanceled() {
		return canceled;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
    
}
