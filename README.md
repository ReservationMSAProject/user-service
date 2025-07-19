# ☕ User Service for Reservation MSA Project

## 📖 프로젝트 소개

본 프로젝트는 **Reservation MSA Project**의 사용자(User) 관련 기능을 전담하는 마이크로서비스입니다.

사용자 인증(로컬 및 소셜 로그인), 회원가입, 정보 관리 등 사용자 계정과 관련된 모든 핵심 기능을 제공합니다. Spring Cloud 환경에서 다른 서비스들과 유기적으로 통신하며, 안정적이고 확장 가능한 사용자 시스템을 구축하는 것을 목표로 합니다.

---

## 🛠️ 기술 스택

### ✅ Backend
- **Java 17**
- **Spring Boot 3.5.3**
- **Spring Security** (JWT, OAuth2 Client)
- **Spring Data JPA**
- **Spring Data Redis**
- **Spring Cloud** (Config, Eureka Client)

### ✅ Database
- **MariaDB** (Primary Storage)
- **Redis** (Refresh Token, Caching)

### ✅ Tools
- **Gradle**
- **Lombok**
- **jjwt** (JSON Web Token)

---

## ✨ 주요 기능

- **사용자 인증**
  - 이메일/비밀번호 기반의 로컬 로그인
  - Google을 이용한 OAuth2 소셜 로그인
  - JWT(Access/Refresh Token)를 이용한 인증 상태 관리
- **회원 관리**
  - 신규 회원가입
  - 이메일 및 휴대폰 번호 중복 확인
  - 사용자 정보 조회 및 수정
  - 비밀번호 재설정
  - 사용자 계정 활성/비활성 처리
- **보안**
  - Spring Security를 통한 인증/인가 관리
  - 비밀번호 암호화 (PasswordEncoder)
  - API 엔드포인트 접근 제어

---

## 📌 API 엔드포인트

### Auth Controller (`/auth/api/v1`)

| Method | URI | 설명 |
| --- | --- | --- |
| `POST` | `/login` | 이메일, 비밀번호로 로그인하고 JWT 토큰을 쿠키에 설정합니다. |
| `POST` | `/logout` | 로그아웃을 처리하고 관련 쿠키를 삭제합니다. |
| `POST` | `/register` | 신규 사용자를 등록합니다. |
| `GET` | `/oauth2/user` | OAuth2 로그인 성공 후 사용자 정보를 조회합니다. |
| `POST` | `/token/valid` | 현재 요청의 JWT 토큰 유효성을 검증합니다. |
| `POST` | `/email/valid` | 이메일 중복 여부를 확인합니다. |
| `POST` | `/phone/valid` | 휴대폰 번호 중복 여부를 확인합니다. |

### User Controller (`/user/api/v1`)

| Method | URI | 설명 |
| --- | --- | --- |
| `GET` | `/me` | 현재 인증된 사용자의 정보를 조회합니다. (`@AuthenticationPrincipal`) |
| `GET` | `/user` | 이메일로 특정 사용자의 정보를 조회합니다. (Query Param: `email`) |
| `GET` | `/users` | 모든 사용자 목록을 조회합니다. |
| `POST` | `/user/update/{id}` | 특정 사용자(ID)의 정보를 수정합니다. |
| `POST` | `/user/active/{id}` | 특정 사용자(ID)의 계정을 활성화/비활성화합니다. |
| `POST` | `/user/password-reset/{id}` | 특정 사용자(ID)의 비밀번호를 재설정합니다. |

---

## 🚀 실행 방법

1.  **저장소 복제**
    ```bash
    git clone https://github.com/ReservationMSAProject/user-service.git
    cd user-service
    ```

2.  **애플리케이션 빌드**
    ```bash
    ./gradlew build
    ```

3.  **애플리케이션 실행**
    ```bash
    java -jar build/libs/user-service-0.0.1-SNAPSHOT.jar
    ```

> **참고:** 애플리케이션을 실행하기 전에 Spring Cloud Config 서버가 먼저 실행되어야 합니다.

---

## 🗄️ 데이터베이스

- **MariaDB**: 사용자 정보, 권한 등 핵심 데이터를 저장하는 주 데이터베이스입니다.
- **Redis**: 발급된 Refresh Token을 저장하여 서버 재시작 및 분산 환경에서도 인증 상태를 유지하는 데 사용됩니다.

---

## ⚙️ 환경 변수 및 설정

본 서비스의 주요 설정은 **Spring Cloud Config Server**를 통해 외부에서 관리됩니다. 로컬에서 실행 시 `application.yaml` 파일에 설정된 Config Server 주소(`http://localhost:8888`)에서 다음 설정 파일들을 가져옵니다.

- `user-service.yml`
- `user-service-db.yml`
- `user-service-oauth2.yml`

필요한 설정 정보는 다음과 같습니다.

- **데이터베이스 연결 정보** (url, username, password)
- **Redis 연결 정보** (host, port)
- **JWT Secret Key**
- **OAuth2 Client ID 및 Secret**
