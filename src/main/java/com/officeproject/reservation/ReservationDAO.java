package com.officeproject.reservation;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.officeproject.util.DBConnection;

//예약 생성 관련 DB 작업

public class ReservationDAO {
	
	//ReservationCreateResult.java의 
	public ReservationCreateResult createReservation(
			
            int memberId,
            int roomId,
            LocalDate useDate
            
    ) throws SQLException {

		//예약 가능한가?
        String roomCheckSql =
                "SELECT 1 "
              + "FROM dbo.room r WITH (UPDLOCK, HOLDLOCK) "
              + "JOIN dbo.room_type rt "
              + "    ON r.room_type_id = rt.room_type_id "
              + "WHERE r.room_id = ? "
              + "  AND r.status = 'AVAILABLE' "
              + "  AND rt.is_active = 1";
        
        //그 날짜에 이미 예약됐나?
        String reservationCheckSql =
                "SELECT 1 "
              + "FROM dbo.reservation WITH (UPDLOCK, HOLDLOCK) "
              + "WHERE room_id = ? "
              + "  AND use_date = ? "
              + "  AND is_canceled = 0";

        //실제 예약
        String insertSql =
                "INSERT INTO dbo.reservation ("
              + "    member_id, "
              + "    room_id, "
              + "    use_date "
              + ") "
              + "VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {

            //autoCommit=false: 예약 가능 확인과 INSERT를 하나의 트랜잭션으로 묶음 > 트랜잭션을 직접 끝내야 함
            conn.setAutoCommit(false);

            try {
                //점검 중, 비활성 유형 공간은 예약못하게
                try (PreparedStatement pstmt = conn.prepareStatement(roomCheckSql)) {
                    pstmt.setInt(1, roomId);
                    
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return ReservationCreateResult.ROOM_NOT_AVAILABLE;
                        }
                    }
                }

                //HOLDLOCK: 확인 후 INSERT가 끝날 때까지 잠금 유지
                try (PreparedStatement pstmt=conn.prepareStatement(reservationCheckSql)) {

                    pstmt.setInt(1, roomId);
                    pstmt.setDate(2, Date.valueOf(useDate));
                    
                    
                    try (ResultSet rs = pstmt.executeQuery()) {
                    	
                    	//예약이 이미 있으면
                        if (rs.next()) {
                            conn.rollback();
                            return ReservationCreateResult.ALREADY_RESERVED;  //enum ReservationCreateResult.java 상태 중 하나
                        }
                    }
                }

                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setInt(1, memberId);
                    pstmt.setInt(2, roomId);
                    pstmt.setDate(3, Date.valueOf(useDate));

                    int result = pstmt.executeUpdate();

                    if (result != 1) {
                        conn.rollback();
                        return ReservationCreateResult.ERROR;
                    }
                }

                conn.commit();
                return ReservationCreateResult.SUCCESS;

                
                
            } catch (SQLException e) {
                conn.rollback();
                
                if (e.getErrorCode() == 2601 || e.getErrorCode() == 2627) {
                    return ReservationCreateResult.ALREADY_RESERVED;
                }

                throw e;

            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
	
	//로그인한 회원의 예약 이력을 조회, 취소된 예약도 함께 보여 주어 사용자가 자신의 이력을 확인할 수 있도록.
    public java.util.List<MyReservation> findByMemberId(int memberId)
            throws SQLException {

        String sql =
                "SELECT "
              + "    rv.reservation_id, "
              + "    r.room_name, "
              + "    rt.type_name, "
              + "    r.floor_no, "
              + "    rv.use_date, "
              + "    rv.is_canceled, "
              + "    rv.created_at "
              + "FROM dbo.reservation rv "
              + "JOIN dbo.room r "
              + "    ON rv.room_id = r.room_id "
              + "JOIN dbo.room_type rt "
              + "    ON r.room_type_id = rt.room_type_id "
              + "WHERE rv.member_id = ? "
              + "ORDER BY rv.use_date DESC, rv.created_at DESC";

        java.util.List<MyReservation> reservations =
                new java.util.ArrayList<>();

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(new MyReservation(
                        rs.getInt("reservation_id"),
                        rs.getString("room_name"),
                        rs.getString("type_name"),
                        rs.getShort("floor_no"),
                        rs.getDate("use_date").toLocalDate(),
                        rs.getBoolean("is_canceled"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                    ));
                }
            }
        }

        return reservations;
    }
	
     //예약 취소.reservation_id만 조건으로 쓰면 다른 회원 예약도 취소할 위험이 있으니 member_id도 함께 WHERE 조건에 넣는다.
     //is_canceled = 0인 유효 예약만 취소 가능하다.
    public boolean cancelReservation(int reservationId, int memberId)
            throws SQLException {

        String sql =
                "UPDATE dbo.reservation "
              + "SET is_canceled = 1 "
              + "WHERE reservation_id = ? "
              + "  AND member_id = ? "
              + "  AND is_canceled = 0";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, reservationId);
            pstmt.setInt(2, memberId);

            int result = pstmt.executeUpdate();

            return result == 1;
        }
    
    }
    
    
    //관리자가 전체 예약을 조회
    //member, room, room_type 테이블을 JOIN > 예약자와 공간 정보를 한 번에 가져옴
    //useDate가 null이면 모든 날의 예약을, 값이 있으면 해당 이용일의 예약만 조회
    public List<AdminReservation> findForAdmin(LocalDate useDate)
            throws SQLException {

        StringBuilder sql = new StringBuilder();

        sql.append("SELECT ");
        sql.append("    rv.reservation_id, ");
        sql.append("    m.member_name, ");
        sql.append("    m.login_id, ");
        sql.append("    r.room_name, ");
        sql.append("    rt.type_name, ");
        sql.append("    r.floor_no, ");
        sql.append("    rv.use_date, ");
        sql.append("    rv.is_canceled, ");
        sql.append("    rv.created_at ");
        sql.append("FROM dbo.reservation rv ");
        sql.append("JOIN dbo.member m ");
        sql.append("    ON rv.member_id = m.member_id ");
        sql.append("JOIN dbo.room r ");
        sql.append("    ON rv.room_id = r.room_id ");
        sql.append("JOIN dbo.room_type rt ");
        sql.append("    ON r.room_type_id = rt.room_type_id ");

        //useDate가 값이 있으면 해당 이용일의 예약만 조회:
        if (useDate != null) {
            sql.append("WHERE rv.use_date = ? ");
        }

        sql.append("ORDER BY rv.use_date DESC, rv.created_at DESC");

        List<AdminReservation> reservations = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            //useDate가 값이 있으면 ? 값에 날짜를 바인딩한다.
            if (useDate != null) {
                pstmt.setDate(1, Date.valueOf(useDate));
            }

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    AdminReservation reservation = new AdminReservation(
                            rs.getInt("reservation_id"),
                            rs.getString("member_name"),
                            rs.getString("login_id"),
                            rs.getString("room_name"),
                            rs.getString("type_name"),
                            rs.getShort("floor_no"),
                            rs.getDate("use_date").toLocalDate(),
                            rs.getBoolean("is_canceled"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );

                    //List<AdminReservation>: DTO
                    reservations.add(reservation);
                }
            }
        }

        return reservations;
    }
    
    //관리자가 예약 취소
    //관리자 기능이므로 member_id 조건은 넣지 않음, 다만 이미 취소된 예약은 다시 UPDATE하지 않도록 is_canceled = 0 조건을 둔다.
    public boolean cancelReservationByAdmin(int reservationId)
            throws SQLException {

        String sql =
                "UPDATE dbo.reservation "
                + "SET is_canceled = 1 "
                + "WHERE reservation_id = ? "
                + "AND is_canceled = 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reservationId);
            return pstmt.executeUpdate() == 1;
        }
    }
    
    
    //선택한 시작일~종료일 사이의 날짜별 유효 예약 건수 조회 > 차트에 사용
    //취소 예약(is_canceled = 1)은 통계에서 제외, GROUP BY에 꼭 use_date.
    public List<DailyReservationStat> findDailyStatistics(
            LocalDate startDate,
            LocalDate endDate) throws SQLException {

        String sql =
                "SELECT "
                + "    rv.use_date, "
                + "    COUNT(*) AS reservation_count "
                + "FROM dbo.reservation rv "
                + "WHERE rv.is_canceled = 0 "
                + "  AND rv.use_date >= ? "
                + "  AND rv.use_date <= ? "
                + "GROUP BY rv.use_date "
                + "ORDER BY rv.use_date ASC";

        List<DailyReservationStat> statistics = new ArrayList<>();

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    statistics.add(new DailyReservationStat(
                            rs.getDate("use_date").toLocalDate(),
                            rs.getInt("reservation_count")
                    ));
                }
            }
        }

        return statistics;
    }

    //선택한 시작일~종료일 사이의 공간 유형별 유효 예약 건수 조회
    //reservation → room → room_type JOIN > 예약된 실제 공간이 어떤 유형인지 알아낸 뒤 유형별로 묶기
    public List<RoomTypeReservationStat> findRoomTypeStatistics(
            LocalDate startDate,
            LocalDate endDate) throws SQLException {

        String sql =
                "SELECT "
                + "    rt.room_type_id, "
                + "    rt.type_name, "
                + "    COUNT(*) AS reservation_count "
                + "FROM dbo.reservation rv "
                + "JOIN dbo.room r "
                + "    ON rv.room_id = r.room_id "
                + "JOIN dbo.room_type rt "
                + "    ON r.room_type_id = rt.room_type_id "
                + "WHERE rv.is_canceled = 0 "
                + "  AND rv.use_date >= ? "
                + "  AND rv.use_date <= ? "
                + "GROUP BY "
                + "    rt.room_type_id, "
                + "    rt.type_name "
                + "ORDER BY "
                + "    reservation_count DESC, "
                + "    rt.type_name ASC";

        List<RoomTypeReservationStat> statistics = new ArrayList<>();

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    statistics.add(new RoomTypeReservationStat(
                            rs.getInt("room_type_id"),
                            rs.getString("type_name"),
                            rs.getInt("reservation_count")
                    ));
                }
            }
        }

        return statistics;
    }
    
    
}
