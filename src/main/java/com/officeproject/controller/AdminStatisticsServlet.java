package com.officeproject.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

import com.officeproject.reservation.DailyReservationStat;
import com.officeproject.reservation.ReservationDAO;
import com.officeproject.reservation.RoomTypeReservationStat;


//관리자가 시작일·종료일을 직접 선택해 날짜별 / 공간 유형별 예약 통계 조회
//GET /admin/statistics?startDate=2026-10-01&endDate=2026-10-31


@WebServlet("/admin/statistics")
public class AdminStatisticsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private final ReservationDAO reservationDAO = new ReservationDAO();

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String startDateParam = request.getParameter("startDate");
        String endDateParam = request.getParameter("endDate");

        LocalDate startDate;
        LocalDate endDate;

        //기본으로 최근 7일이 기본 조회 기간
        //이후 화면에서 시작일,종료일 자유롭게 변경
        if ((startDateParam==null || startDateParam.isBlank())
                && (endDateParam==null || endDateParam.isBlank())) {

            endDate=LocalDate.now();
            startDate=endDate.minusDays(6);

        } else {
            try {
                //한쪽 날짜만 입력한 경우
                if (startDateParam == null || startDateParam.isBlank()
                        || endDateParam == null || endDateParam.isBlank()) {

                    throw new DateTimeParseException(
                            "날짜를 모두 입력해야 합니다.",
                            "",
                            0
                    );
                }

                startDate=LocalDate.parse(startDateParam);
                endDate=LocalDate.parse(endDateParam);

            } catch (DateTimeParseException e) {
                showValidationError(
                        request,
                        response,
                        "시작일과 종료일을 모두 올바르게 입력해 주세요."
                );
                return;
            }
        }

        //시작일이 종료일보다 뒤
        if (startDate.isAfter(endDate)) {
            showValidationError(
                    request,
                    response,
                    "시작일은 종료일보다 늦을 수 없습니다."
            );
            return;
        }

        //너무 넓은 기간의 집계를 막기. 최대 1년(366일)까지 조회 가능
        if (startDate.plusDays(366).isBefore(endDate)) {
            showValidationError(
                    request,
                    response,
                    "통계 조회 기간은 최대 366일까지 선택할 수 있습니다."
            );
            return;
        }

        try {
            List<DailyReservationStat> dailyStatistics =
                    reservationDAO.findDailyStatistics(startDate, endDate);

            List<RoomTypeReservationStat> roomTypeStatistics =
                    reservationDAO.findRoomTypeStatistics(startDate, endDate);

            request.setAttribute("startDate", startDate);
            request.setAttribute("endDate", endDate);
            request.setAttribute("dailyStatistics", dailyStatistics);
            request.setAttribute("roomTypeStatistics", roomTypeStatistics);

            request.getRequestDispatcher(
                    "/WEB-INF/views/adminStatistics.jsp"
            ).forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "이용 통계를 조회하는 중 오류가 발생했습니다.",
                    e
            );
        }
	}
    
    //날짜 검증 실패 시에도 JSP가 필요한 속성을 안전하게 사용
    //빈 목록을 전달하므로 JSP의 forEach가 오류 없이 처리
    private void showValidationError(
            HttpServletRequest request,
            HttpServletResponse response,
            String errorMessage)
            throws ServletException, IOException {

        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("dailyStatistics", Collections.emptyList());
        request.setAttribute("roomTypeStatistics", Collections.emptyList());

        request.getRequestDispatcher("/WEB-INF/views/adminStatistics.jsp").forward(request, response);
    }

}
