<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>이용 통계 | Office Mate</title>
	
	<!--모든 페이지 적용 기본 스타일-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/common.css">

    <!--헤더, 메뉴 등 레이아웃-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/layout.css">
    
    <!-- 관리자 대시보드·관리 목록 화면 전용 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/admin.css">
    
</head>
<body>
	<header class="header">
        <div class="header-inner">
            <a class="logo"
               href="${pageContext.request.contextPath}/main">
                Office Mate
            </a>

            <nav class="menu">
                <a href="${pageContext.request.contextPath}/main">홈</a>
                <a href="${pageContext.request.contextPath}/rooms">공간 예약</a>
                <a href="${pageContext.request.contextPath}/reservation/my">내 예약</a>

                <a href="${pageContext.request.contextPath}/admin"
                   class="is-active">
                    관리자
                </a>

                <a href="${pageContext.request.contextPath}/logout">로그아웃</a>
            </nav>
        </div>
    </header>

    <main class="container admin-statistics-page">

        <div class="page-heading">
            <p class="page-eyebrow">ADMIN</p>
            <h1>이용 통계</h1>
            <p>선택한 기간의 유효 예약 데이터를 날짜와 공간 유형별로 집계합니다.</p>
        </div>

        <%-- GET 방식: URL에 startDate와 endDate가 남아 결과를 공유·새로고침할 수 있다. --%>
        <form method="get"
              action="${pageContext.request.contextPath}/admin/statistics"
              class="statistics-search-form">

            <div class="form-field">
                <label for="startDate">시작일</label>
                <input type="date"
                       id="startDate"
                       name="startDate"
                       value="<c:out value='${startDate}' />"
                       required>
            </div>

            <div class="form-field">
                <label for="endDate">종료일</label>
                <input type="date"
                       id="endDate"
                       name="endDate"
                       value="<c:out value='${endDate}' />"
                       required>
            </div>

            <div class="statistics-search-button">
                <button type="submit" class="button button-primary">
                    통계 조회
                </button>
            </div>
        </form>

        <c:if test="${not empty errorMessage}">
            <p class="form-error">
                <c:out value="${errorMessage}" />
            </p>
        </c:if>

        <%--
            통계 값을 JavaScript 코드에 직접 이어 붙이지 않는다.
            HTML data-* 속성에 안전하게 출력한 뒤,
            아래 JavaScript가 DOM에서 값을 읽어 Chart.js에 전달한다.
        --%>
        <div id="statisticsData" hidden>

            <c:forEach var="dailyStat" items="${dailyStatistics}">
                <span class="daily-stat-data"
                      data-date="<c:out value='${dailyStat.useDate}' />"
                      data-count="<c:out value='${dailyStat.reservationCount}' />">
                </span>
            </c:forEach>

            <c:forEach var="roomTypeStat" items="${roomTypeStatistics}">
                <span class="room-type-stat-data"
                      data-type-name="<c:out value='${roomTypeStat.typeName}' />"
                      data-count="<c:out value='${roomTypeStat.reservationCount}' />">
                </span>
            </c:forEach>

        </div>

        <section class="statistics-grid">

            <article class="statistics-card">
                <div class="statistics-card-heading">
                    <h2>날짜별 예약 건수</h2>
                    <p>
                        <c:out value="${startDate}" />
                        ~
                        <c:out value="${endDate}" />
                    </p>
                </div>

                <c:choose>
                    <c:when test="${empty dailyStatistics}">
                        <div class="empty-state">
                            <p>선택한 기간에 유효한 예약이 없습니다.</p>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <div class="chart-container">
                            <canvas id="dailyReservationChart"></canvas>
                        </div>
                    </c:otherwise>
                </c:choose>
            </article>

            <article class="statistics-card">
                <div class="statistics-card-heading">
                    <h2>공간 유형별 예약 건수</h2>
                    <p>취소되지 않은 예약 기준</p>
                </div>

                <c:choose>
                    <c:when test="${empty roomTypeStatistics}">
                        <div class="empty-state">
                            <p>선택한 기간에 유효한 예약이 없습니다.</p>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <div class="chart-container">
                            <canvas id="roomTypeReservationChart"></canvas>
                        </div>
                    </c:otherwise>
                </c:choose>
            </article>

        </section>

        <section class="statistics-table-section">
            <h2>집계 원본 데이터</h2>

            <div class="statistics-table-grid">

                <div class="table-scroll">
                    <table class="statistics-table">
                        <thead>
                            <tr>
                                <th>이용일</th>
                                <th>예약 건수</th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:forEach var="dailyStat" items="${dailyStatistics}">
                                <tr>
                                    <td>
                                        <c:out value="${dailyStat.useDate}" />
                                    </td>
                                    <td>
                                        <c:out value="${dailyStat.reservationCount}" />건
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <div class="table-scroll">
                    <table class="statistics-table">
                        <thead>
                            <tr>
                                <th>공간 유형</th>
                                <th>예약 건수</th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:forEach var="roomTypeStat"
                                       items="${roomTypeStatistics}">
                                <tr>
                                    <td>
                                        <c:out value="${roomTypeStat.typeName}" />
                                    </td>
                                    <td>
                                        <c:out value="${roomTypeStat.reservationCount}" />건
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

            </div>
        </section>
    </main>

    <%-- Chart.js 외부 라이브러리. 인터넷 연결이 있어야 그래프가 표시된다. --%>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    <script>
        /*
         * JSP가 만든 data-* 속성에서 날짜와 예약 건수를 읽는다.
         * 날짜·숫자 데이터를 JS 코드에 직접 문자열로 붙이지 않아
         * 따옴표·특수문자 문제를 줄인다.
         */
        const dailyStatElements =
                document.querySelectorAll(".daily-stat-data");

        const dailyLabels = Array.from(dailyStatElements)
                .map((element) => element.dataset.date);

        const dailyCounts = Array.from(dailyStatElements)
                .map((element) => Number(element.dataset.count));

        const roomTypeStatElements =
                document.querySelectorAll(".room-type-stat-data");

        const roomTypeLabels = Array.from(roomTypeStatElements)
                .map((element) => element.dataset.typeName);

        const roomTypeCounts = Array.from(roomTypeStatElements)
                .map((element) => Number(element.dataset.count));

        /*
         * 일별 예약 건수 선 그래프
         */
        const dailyChartCanvas =
                document.getElementById("dailyReservationChart");

        if (dailyChartCanvas && dailyLabels.length > 0
                && typeof Chart !== "undefined") {

            new Chart(dailyChartCanvas, {
                type: "line",

                data: {
                    labels: dailyLabels,

                    datasets: [
                        {
                            label: "예약 건수",
                            data: dailyCounts,
                            borderColor: "#2563eb",
                            backgroundColor: "rgba(37, 99, 235, 0.12)",
                            borderWidth: 2,
                            tension: 0.25,
                            fill: true,
                            pointRadius: 4,
                            pointBackgroundColor: "#2563eb"
                        }
                    ]
                },

                options: {
                    responsive: true,
                    maintainAspectRatio: false,

                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                precision: 0
                            }
                        }
                    }
                }
            });
        }

        /*
         * 공간 유형별 예약 건수 막대 그래프
         */
        const roomTypeChartCanvas =
                document.getElementById("roomTypeReservationChart");

        if (roomTypeChartCanvas && roomTypeLabels.length > 0
                && typeof Chart !== "undefined") {

            new Chart(roomTypeChartCanvas, {
                type: "bar",

                data: {
                    labels: roomTypeLabels,

                    datasets: [
                        {
                            label: "예약 건수",
                            data: roomTypeCounts,
                            backgroundColor: [
                                "#2563eb",
                                "#38bdf8",
                                "#14b8a6",
                                "#8b5cf6",
                                "#f59e0b",
                                "#ef4444"
                            ],
                            borderRadius: 6
                        }
                    ]
                },

                options: {
                    responsive: true,
                    maintainAspectRatio: false,

                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                precision: 0
                            }
                        }
                    }
                }
            });
        }
    </script>
</body>
</html>