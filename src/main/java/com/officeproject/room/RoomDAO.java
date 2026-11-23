package com.officeproject.room;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.officeproject.util.DBConnection;

//room, room_type, reservation 테이블 조회 담당

public class RoomDAO {

    //선택한 날짜에 예약 가능한 공간 조회
	//RoomListServlet.java 에서 사용
    //NOT EXISTS: 해당 room_id와 use_date에 유효한 예약(is_canceled = 0)이 하나라도 존재하면 그 공간은 결과에서 제외한다.
    public List<Room> findAvailableRooms(LocalDate useDate)
            throws SQLException {

        String sql =
        		"SELECT "
        		+ "    r.room_id, "
        		+ "    r.room_name, "
        		+ "    r.image_path, "
        		+ "    r.floor_no, "
        		+ "    rt.type_name, "
        		+ "    rt.max_capacity "
        		+ "FROM dbo.room r "
        		+ "JOIN dbo.room_type rt "
        		+ "    ON r.room_type_id = rt.room_type_id "
        		+ "WHERE r.status = 'AVAILABLE' "
        		+ "  AND rt.is_active = 1 "
        		+ "  AND NOT EXISTS ( "
        		+ "      SELECT 1 "
        		+ "      FROM dbo.reservation rv "
        		+ "      WHERE rv.room_id = r.room_id "
        		+ "        AND rv.use_date = ? "
        		+ "        AND rv.is_canceled=0 "
        		+ "  ) "
        		+ "ORDER BY r.floor_no, r.room_name";

        
        List<Room> rooms = new ArrayList<>();

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
        	//useDate는 LocalDate
        	//Date(java.sql.Date)는 JDBC로 DB의 DATE 값을 주고받을 때 사용하는 타입(use_date date NOT NULL,)
            pstmt.setDate(1, Date.valueOf(useDate));

            //executeQuery는 조회.
            try (ResultSet rs = pstmt.executeQuery()) {
            	//
                while (rs.next()) {
                	//Room.java JavaBean 객체 roomId, roomName, imagePath, floorNo, typeName, maxCapacity
                    rooms.add(new Room(
                    	rs.getInt("room_id"),
                        rs.getString("room_name"),
                        rs.getString("image_path"),
                        rs.getShort("floor_no"),
                        rs.getString("type_name"),
                        rs.getShort("max_capacity")
                    ));
                }
            }
        }

        return rooms;
    }
    
    //관리자가 전체 공간 조회
    //AdminRoomListServlet.java에 전체 공간 객체들 전달
    //일반 사용자용 findAvailableRooms()와 달리 점검 중인 공간(OUT_OF_SERVICE)도 함께 조회
    public List<AdminRoom> findAllForAdmin()
            throws SQLException {

        String sql =
                "SELECT "
                + "    r.room_id, "
                + "    r.room_name, "
                + "    rt.type_name, "
                + "    rt.max_capacity, "
                + "    r.floor_no, "
                + "    r.status, "
                + "    r.image_path "
                + "FROM dbo.room r "
                + "JOIN dbo.room_type rt "
                + "    ON r.room_type_id = rt.room_type_id "
                + "ORDER BY r.floor_no, r.room_name";

        List<AdminRoom> rooms = new ArrayList<>();

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {

                	//roomId, roomName, typeName, maxCapacity, floorNo, status, imagePath
                    rooms.add(new AdminRoom(
                            rs.getInt("room_id"),
                            rs.getString("room_name"),
                            rs.getString("type_name"),
                            rs.getShort("max_capacity"),
                            rs.getShort("floor_no"),
                            rs.getString("status"),
                            rs.getString("image_path")
                    ));
                }
            }
        }

        return rooms;
    }

    //관리자가 공간 운영 상태 변경
    //AdminRoomStatusServlet.java에서 호출
    public boolean updateRoomStatus(
            int roomId,
            String status) throws SQLException {

        //Servlet뿐 아니라 DAO에서도 허용 상태를 한 번 더 검증한다.-잘못된 문자열이 DB까지 전달되는 것을 막는 방어 코드
    	//AVAILABLE도 아니고 OUT_OF_SERVICE도 아닌거 쳐내기
        if (!"AVAILABLE".equals(status) && !"OUT_OF_SERVICE".equals(status)) {
            return false;
        }

        String sql =
                "UPDATE dbo.room "
                + "SET status = ? "
                + "WHERE room_id = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, status);
            pstmt.setInt(2, roomId);

            //수정된 행이 정확히 1개면 true다.
            return pstmt.executeUpdate() == 1;
        }
    }
    
    
}
