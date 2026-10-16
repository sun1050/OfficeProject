package com.officeproject.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;

//내 예약 목록 화면에 표시할 reservation + room + room_type JOIN 결과 객체.

public class MyReservation {

	private final int reservationId;
    private final String roomName;
    private final String typeName;
    private final short floorNo;
    private final LocalDate useDate;
    private final boolean canceled;
    private final LocalDateTime createdAt;
    
    public MyReservation(int reservationId, String roomName, String typeName, short floorNo, LocalDate useDate, boolean canceled, LocalDateTime createdAt) {
        this.reservationId = reservationId;
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
