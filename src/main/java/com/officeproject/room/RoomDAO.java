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
            pstmt.setDate(1, Date.valueOf(useDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                	//Room.java JavaBean 객체
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
     /*
     * AVAILABLE: 예약 가능한 정상 운영 공간
     * OUT_OF_SERVICE: 점검·고장 등으로 예약 불가 공간
     */
    public boolean updateRoomStatus(
            int roomId,
            String status) throws SQLException {

        //Servlet뿐 아니라 DAO에서도 허용 상태를 한 번 더 검증한다.-잘못된 문자열이 DB까지 전달되는 것을 막는 방어 코드
        
        if (!"AVAILABLE".equals(status)
                && !"OUT_OF_SERVICE".equals(status)) {
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
