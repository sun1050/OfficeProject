package com.officeproject.room;

//관리자가 공간 목록을 조회하고 상태를 관리 때 사용 DTO객체
//AVAILABLE / OUT_OF_SERVICE 상태를 포함
public class AdminRoom {
	private final int roomId;
    private final String roomName;
    private final String typeName;
    private final short maxCapacity;
    private final short floorNo;
    private final String status;
    private final String imagePath;
    
    
    public AdminRoom(int roomId,String roomName,String typeName,short maxCapacity,short floorNo,
            String status,String imagePath) {

        this.roomId = roomId;
        this.roomName = roomName;
        this.typeName = typeName;
        this.maxCapacity = maxCapacity;
        this.floorNo = floorNo;
        this.status = status;
        this.imagePath = imagePath;
    }
    
    
	public int getRoomId() {
		return roomId;
	}
	public String getRoomName() {
		return roomName;
	}
	public String getTypeName() {
		return typeName;
	}
	public short getMaxCapacity() {
		return maxCapacity;
	}
	public short getFloorNo() {
		return floorNo;
	}
	public String getStatus() {
		return status;
	}
	public String getImagePath() {
		return imagePath;
	}
	
}
