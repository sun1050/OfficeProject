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
