package com.officeproject.reservation;

//공간 유형별 예약 건수 통계 DTO
/*
 * 1인 프라이빗룸 / 12건
 * 소회의실 / 7건
 */
public class RoomTypeReservationStat {
	
	private final int roomTypeId;
    private final String typeName;
    private final int reservationCount;
    
    public RoomTypeReservationStat(int roomTypeId,String typeName,int reservationCount) {

        this.roomTypeId = roomTypeId;
        this.typeName = typeName;
        this.reservationCount = reservationCount;
    }
    
    
	public int getRoomTypeId() {
		return roomTypeId;
	}
	public String getTypeName() {
		return typeName;
	}
	public int getReservationCount() {
		return reservationCount;
	}

    
    
}
