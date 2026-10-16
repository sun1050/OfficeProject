package com.officeproject.room;

//JavaBean

//room+room_type 테이블 정보 담는 객체
public class Room {
	
	private final int roomId;  //room_id(room)
    private final String roomName;  //room_name(room)
    private final String imagePath;  //image_path(room)
    private final short floorNo;  //floor_no(room)
    private final String typeName;  //type_name(room_type)
    private final short maxCapacity;  //max_capacity(room_type)
    
    public Room(int roomId, String roomName, String imagePath, short floorNo, String typeName, short maxCapacity
    ) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.imagePath = imagePath;
        this.floorNo = floorNo;
        this.typeName = typeName;
        this.maxCapacity = maxCapacity;
    }
    
	public int getRoomId() {
		return roomId;
	}
	public String getRoomName() {
		return roomName;
	}
	public String getImagePath() {
		return imagePath;
	}
	public short getFloorNo() {
		return floorNo;
	}
	public String getTypeName() {
		return typeName;
	}
	public short getMaxCapacity() {
		return maxCapacity;
	}
    

}
