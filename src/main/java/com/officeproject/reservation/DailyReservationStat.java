package com.officeproject.reservation;

import java.time.LocalDate;

//날짜별 예약 건수 통계 DTO
//GROUP BY use_date 결과의 한 행을 표현(2026-10-16 / 4건)
public class DailyReservationStat {
	private final LocalDate useDate;
    private final int reservationCount;
    
    public DailyReservationStat(LocalDate useDate,int reservationCount) {

        this.useDate = useDate;
        this.reservationCount = reservationCount;
    }
    
	public LocalDate getUseDate() {
		return useDate;
	}
	public int getReservationCount() {
		return reservationCount;
	}

    
}
