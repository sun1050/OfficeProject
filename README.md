# Office Mate

JSP · Servlet · JDBC 기반의 단일 지점 공유오피스 공간 예약·운영 관리 포트폴리오입니다.

## 기술 환경

- Java 17, Apache Tomcat 10.1
- JSP, Jakarta Servlet, JSTL/EL
- JDBC, Microsoft SQL Server Express
- BCrypt, Chart.js
- Eclipse Dynamic Web Project

## 주요 기능

- 회원가입, BCrypt 비밀번호 해시 검증, 로그인·로그아웃
- 세션 기반 회원·관리자 권한 확인
- 날짜별 예약 가능한 공간 조회, 예약 생성, 본인 예약 조회·취소
- 관리자 전체 예약 조회·취소, 공간 상태 변경
- 이용 통계 화면: 날짜별·공간 유형별 예약 통계 (실제 동작 검증 진행 중)

## 프로젝트 구조

```text
src/main/java/com/officeproject/
  controller/    요청 처리 Servlet
  filter/        회원·관리자 권한 필터
  member/        회원 모델 및 DAO
  reservation/   예약 모델 및 DAO
  room/          공간 모델 및 DAO
  util/          DB 연결 등 공통 기능
src/main/webapp/
  WEB-INF/views/ JSP 화면
  WEB-INF/lib/   JDBC, JSTL, BCrypt 라이브러리
  resources/     CSS 및 이미지
```

요청은 `URL → Servlet → DAO → JSP` 순서로 처리합니다. JSP는 `WEB-INF/views` 아래에 두어 직접 URL로 접근할 수 없게 합니다.

## 로컬 실행

1. Eclipse에서 **File → Import → General → Existing Projects into Workspace**로 프로젝트를 가져옵니다.
2. Java 17과 Tomcat 10.1 런타임을 연결합니다.
3. SQL Server에 DB와 `member`, `room_type`, `room`, `reservation` 테이블을 준비합니다. 현재 저장소에는 DB 생성 SQL과 초기 데이터가 포함되어 있지 않습니다.
4. Eclipse Tomcat의 **Run Configurations → Environment**에 아래 환경변수를 설정합니다.

| 환경변수 | 값 |
| --- | --- |
| `OFFICE_DB_URL` | 사용할 SQL Server JDBC 접속 URL |
| `OFFICE_DB_USER` | DB 접속 계정 |
| `OFFICE_DB_PASSWORD` | DB 접속 비밀번호 |

5. Tomcat에 프로젝트를 추가하고 실행합니다. HTTP 포트를 8888로 설정한 경우 `http://localhost:8888/OfficeProject/`로 접속합니다.

환경변수는 Tomcat 프로세스가 읽습니다. `.env` 파일을 자동으로 읽는 기능은 현재 없습니다. 실제 인증 정보는 소스나 README에 작성하지 않습니다.

## Git 관리

소스, 정적 리소스, 실행에 필요한 `WEB-INF/lib`의 JAR와 Eclipse 프로젝트 설정은 저장합니다. 빌드 결과물, `.env`, 로컬 실행 설정(`*.launch`), 로그 및 DB 백업 파일은 `.gitignore`로 제외합니다.

현재 로컬 개발 단계이며, 이번 Git 등록 준비 과정에서는 DB 연결 및 웹 기능 실행을 검증하지 않았습니다.


## 데이터베이스 설계

Microsoft SQL Server를 사용하며, 회원·공간 유형·공간·예약을 다음 네 테이블로 관리합니다.

### 테이블 구성

| 테이블 | 역할 |
|---|---|
| `member` | 회원 계정과 일반 회원·관리자 역할 관리 |
| `room_type` | 공간 유형, 최대 인원, 활성 여부 관리 |
| `room` | 실제 예약 대상 공간과 운영 상태 관리 |
| `reservation` | 회원별 공간 예약과 취소 이력 관리 |

### member — 회원

| 컬럼 | 자료형 | 설명 |
|---|---|---|
| `member_id` | INT | PK, IDENTITY(1,1) |
| `login_id` | VARCHAR(30) | 로그인 ID, UNIQUE |
| `password_hash` | VARCHAR(100) | BCrypt 비밀번호 해시 |
| `member_name` | NVARCHAR(30) | 회원 이름 |
| `role` | VARCHAR(10) | `MEMBER` 또는 `ADMIN`, 기본값 `MEMBER` |
| `created_at` | DATETIME2(0) | 생성 시각, 기본값 `SYSDATETIME()` |

### room_type — 공간 유형

| 컬럼 | 자료형 | 설명 |
|---|---|---|
| `room_type_id` | INT | PK, IDENTITY(1,1) |
| `type_name` | NVARCHAR(50) | 공간 유형명, UNIQUE |
| `max_capacity` | SMALLINT | 최대 인원, 0보다 큰 값 |
| `description` | NVARCHAR(200) | 공간 유형 설명, NULL 허용 |
| `is_active` | BIT | 활성 여부, 기본값 1 |
| `created_at` | DATETIME2(0) | 생성 시각, 기본값 `SYSDATETIME()` |

### room — 공간

| 컬럼 | 자료형 | 설명 |
|---|---|---|
| `room_id` | INT | PK, IDENTITY(1,1) |
| `room_type_id` | INT | FK → `room_type.room_type_id` |
| `room_name` | VARCHAR(30) | 공간 이름, UNIQUE |
| `image_path` | VARCHAR(255) | 이미지 경로, NULL 허용 |
| `floor_no` | SMALLINT | 층수, -5~100 범위에서 0 제외 |
| `status` | VARCHAR(20) | `AVAILABLE` 또는 `OUT_OF_SERVICE`, 기본값 `AVAILABLE` |
| `created_at` | DATETIME2(0) | 생성 시각, 기본값 `SYSDATETIME()` |

### reservation — 예약

| 컬럼 | 자료형 | 설명 |
|---|---|---|
| `reservation_id` | INT | PK, IDENTITY(1,1) |
| `member_id` | INT | FK → `member.member_id` |
| `room_id` | INT | FK → `room.room_id` |
| `use_date` | DATE | 공간 이용일 |
| `is_canceled` | BIT | 0: 유효 예약, 1: 취소 이력, 기본값 0 |
| `created_at` | DATETIME2(0) | 생성 시각, 기본값 `SYSDATETIME()` |

별도로 NULL 허용을 표시한 컬럼 외에는 모두 NOT NULL입니다.

### 테이블 관계

- 회원 한 명은 여러 예약을 가질 수 있습니다.
- 공간 유형 하나에 여러 공간이 연결됩니다.
- 공간 하나는 날짜별로 여러 예약 이력을 가질 수 있습니다.
- 예약은 회원 한 명과 공간 하나를 참조합니다.

예약 취소는 행을 삭제하지 않고 `is_canceled = 1`로 변경해 이력을 보존합니다.

## 인덱스 설계 및 성능 검증

기존 PK·UNIQUE 인덱스를 유지한 상태에서, 실제 DAO의 조회 조건과 정렬 순서를 기준으로 추가 인덱스를 검토했습니다.

### 1. 회원별 예약 조회 인덱스

```sql
CREATE INDEX IX_reservation_member_use_date_created_at
ON dbo.reservation(
    member_id,
    use_date DESC,
    created_at DESC
)
INCLUDE(room_id, is_canceled);
```

사용 대상: `ReservationDAO.findByMemberId()`

```sql
WHERE rv.member_id = ?
ORDER BY rv.use_date DESC, rv.created_at DESC
```

회원 번호로 조회 범위를 좁히고 이용일·생성 시각 내림차순 정렬을 지원합니다. JOIN과 상태 표시에 필요한 `room_id`, `is_canceled`를 포함합니다.

### 2. 날짜별 예약 조회 인덱스

```sql
CREATE INDEX IX_reservation_use_date_created_at
ON dbo.reservation(
    use_date,
    created_at DESC
)
INCLUDE(member_id, room_id, is_canceled);
```

사용 대상:

- `RoomDAO.findAvailableRooms()` — 선택 날짜의 예약 존재 여부 확인
- `ReservationDAO.findForAdmin()` — 관리자 날짜 검색
- `ReservationDAO.findDailyStatistics()` — 날짜별 예약 통계
- `ReservationDAO.findRoomTypeStatistics()` — 공간 유형별 예약 통계

이용일의 일치·기간 조건으로 조회 범위를 좁히고, 인덱스에 포함된 회원·공간 번호와 취소 여부를 조회에 활용합니다. `created_at DESC`는 관리자 날짜 검색의 정렬도 지원하기 위한 구성입니다.

### 성능 측정 조건

- SQL Server Express의 별도 테스트 DB 사용
- 합성 회원 10,000명, 공간 유형 4개, 공간 200개, 예약 730,000건
- 동일한 데이터·SQL·검색 조건에서 추가 인덱스 적용 전후 비교
- SQL별 준비 실행 2회 후 15회 측정한 중앙값 사용
- `SET STATISTICS IO`, `SET STATISTICS TIME`, 실제 실행 계획 확인

테스트 데이터는 확대 실험용이며, 실제 단일 지점의 운영 규모를 의미하지 않습니다.

### 주요 조회 결과

날짜 검색은 2026-10-20, 월간 통계는 2026-10-01~31 조건입니다.

| 조회 | 예약 테이블 논리적 읽기 Before → After | DB 경과 시간 중앙값 Before → After |
|---|---:|---:|
| 예약 가능 공간 | 2,809 → 3페이지 | 32 → 0ms |
| 내 예약 1,173건 | 2,809 → 8페이지 | 30 → 2ms |
| 관리자 날짜 검색 | 2,809 → 3페이지 | 31 → 2ms |
| 날짜별 월간 통계 | 2,809 → 26페이지 | 35 → 3ms |
| 공간 유형별 월간 통계 | 2,809 → 26페이지 | 34 → 2ms |

예약 테이블 접근이 Clustered Index Scan에서 Index Seek로 변경됐습니다. `0ms`는 측정 정밀도에서 보고된 값이며 실제 실행 시간이 전혀 없다는 뜻은 아닙니다.

논리적 읽기 감소는 해당 쿼리의 데이터 페이지 접근량 감소를 의미하며, 프로그램 전체 메모리 사용량 감소를 의미하지 않습니다.

### 인덱스 선정 시 고려한 비용

취소되지 않은 예약만 포함하는 다음 필터 인덱스도 후보로 비교했습니다.

```sql
-- 비교 후보이며 최종 기본안에서는 제외
CREATE INDEX IX_reservation_active_date_room
ON dbo.reservation(use_date, room_id)
WHERE is_canceled = 0;
```

이 필터 인덱스를 제외해도 테스트한 11개 조회 시나리오의 논리적 읽기량이 같았습니다. 따라서 추가 저장 공간과 쓰기 유지비용을 고려해 최종 기본안은 두 개로 선정했습니다.

최종 두 인덱스도 쓰기 비용은 증가했습니다.

| 단건 SQL 1,000회 배치 | Before 중앙값 | 인덱스 2개 적용 후 중앙값 |
|---|---:|---:|
| INSERT | 15.876ms | 20.423ms |
| 본인 예약 취소 | 15.796ms | 19.007ms |

쓰기 검사는 서버 측 반복문으로 실행하고 각 표본을 롤백했습니다. JDBC 1,000회 왕복이나 COMMIT 지연을 측정한 값은 아닙니다.

조회 성능 개선과 함께 저장 공간·쓰기 비용도 고려했습니다.
