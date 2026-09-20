# Chat Application Server Kotlin

채팅 시스템의 서버로 Flutter 앱과 통신하며, REST API와 WebSocket 기반의 실시간 채팅 기능을 제공합니다.
코드의 생산성과 안정성 향상을 위하여 기존에 Java로 구현하였던 서버를 Kotlin으로 마이그레이션 하였으며, Amazon EC2에 배포하였습니다.


## 주요 기능

### [기능별 비즈니스 로직](docs/business-rule.md)

#### Authentication
- 사용자 인증 토큰(JSON Web Token) 발급 및 검증
#### User
- 회원가입/로그인
  - 소셜 로그인(OIDC(OpenID Connect))을 통한 회원가입/로그인
  - 로그인 시 사용자 인증을 위한 토큰(JSON Web Token) 발급
- 회원탈퇴
  - 회원탈퇴 시 사용자 비활성
- 회원정보 수정
  - 프로필 수정
  - 계정 아이디 수정

#### Friend
- 친구 추가할 사용자 검색
- 친구 추가/삭제

#### Chat
- 채팅방 생성/삭제
- 채팅방 입장/퇴장
- 실시간 메시지 전송

## 개선 사항
- Null safety﻿를 통한 코드 안정성 확보 및 간결화
- 읽기만 필요한 변수는 val로 선언하여 코드의 안전성 개선
- DTO 구조 개선(용도에 따라 별도 클래스로 분리)
- 웹소켓 연결시 사용자 인증 토큰 검증 로직 개선

## 사용 기술
- Kotlin 1.9<br>
- Spring Boot, Spring Data JPA (Hibernate), Spring Security<br>
- MariaDB<br>
- WebSocket(STOMP)<br>
- Amazon EC2

## API
<details>
<summary>보기</summary>

### [Swagger API 문서](https://my-chat-test.duckdns.org/api/swagger-ui/index.html#/)
>서버 운영 여부에 따라 접속이 어려울 수 있습니다.

### REST API
#### Authentication
| 기능 | Method | URL | 
|---|---|---|
| 로그인 | POST | `/auth/login`|
| 토큰 재발급 | POST | `/auth/refresh`| 

#### User
| 기능 | Method | URL |
|---|---|---|
| 내 프로필 조회 | GET | `/users/me/profile`| 
| 사용자 프로필 조회 | GET | `/users/{userId}/profile`| 
| 계정아이디(username) 사용 가능 여부 조회 | GET | `/users/username/availability`| 
| 내 프로필 수정 | PUT |  `/users/me/profile`| 
| 내 계정아이디(username) 수정| PUT | `/users/me/username`| 
| 회원탈퇴 | DELETE | `/users/me`| 

#### Friend
| 기능 | Method | URL |
|---|---|---|
| 친구 목록 조회 | GET |`/friends`| 
| 친구로 등록할 사용자 조회 | GET | `/friends/search`| 
| 친구 추가 | POST |`/friends`| 
| 친구 삭제 | DELETE | `/friends/{targetUserId}`| 

#### Chat Room
| 기능 | Method | URL | 
|---|---|---|
| 채팅방 목록 조회 | GET |`/chat-rooms`| 
| 채팅방 조회 및 생성 | POST |`/chat-rooms `|
| 채팅방 나가기 | DELETE |`/chat-rooms/{chatRoomId}/members/me`|

#### Chat Message
| 기능 | Method | URL | 
|---|---|---|
| 메시지 조회 | GET | `/chat-messages/{chatRoomId}` |

### WebSocket API
| 기능 | STOMP Command | Destination |
|---|---|---|
| 채팅 메시지 전송 | SEND | `/app/message` |
| 채팅 메시지 구독 | SUBSCRIBE | `/topic/chatroom/{chatRoomId}` |
</details>

## ERD
<details>
<summary>보기</summary>
<img width="605" height="481" alt="Screenshot 2026-06-02 at 11 00 13 PM" src="https://github.com/user-attachments/assets/6cb2c14f-b1ec-484c-97f1-106c4a7c92fa" />
</details>

## 프로젝트를 진행하며 고민한 사항
<details>
<summary>보기</summary>

### 1. 소셜 로그인 시 인증 책임을 클라이언트와 서버 중 어느 곳에 둘 것인가?
- 구현 방식
  - A: 클라이언트에서 OIDC(OpenID Connect) 인증 후 ID Token 획득
  - B: 서버에서  OIDC(OpenID Connect) 인증 후 ID Token 획득
- 선택
  - A: 클라이언트에서 ID Token 획득 후 서버로 전송, 서버에서 검증 후 JWT 발급
- 이유
  - 모바일 환경에 적합한 사용자 경험 제공
  - 서버는 ID Token 검증과 자체 JWT 발급에만 집중하도록 함

### 2. 채팅방을 언제 생성할 것인가?
- 구현 방식
  - A: HTTP 요청으로 채팅방 생성 후, WebSocket 요청으로 메시지 전송
  - B: 채팅방 생성과 메시지 전송을 WebSocket 요청에서 처리<br>
    \* 사용자 경험은 동일함
- 선택
  - A: HTTP 요청으로 채팅방 생성 후, WebSocket 요청으로 메시지 전송
- 이유
  - 채팅방 생성과 메시지 전송 요청 분리
  - 메시지 처리 로직 단순화

### 3. 채팅방 퇴장 시 사용자의 참여 상태를 어떻게 관리할 것인가?
- 구현 방식
  - A: 퇴장한 사용자의 참여 상태만 변경 (Soft Delete)
  - B: 퇴장한 사용자를 채팅방 멤버 목록에서 삭제 (Hard Delete)
- 선택
  - A: 퇴장한 사용자의 참여 상태만 변경 (Soft Delete)
- 이유
  - 채팅방 멤버 목록에서 삭제(Hard Delete) 할 경우 동일 사용자 간 채팅방과 메시지의 중복 생성 문제가 있음

### 4. Access Token 만료를 어떻게 감지하고 갱신할 것인가?
- 구현 방식
  - A: HTTP 요청 시 401을 응답받은 경우 클라이언트에서 재발급 요청
  - B: 클라이언트에서 Access Token 만료 전 갱신 요청
- 선택
  - A: HTTP 요청 시 401을 응답받은 경우 클라이언트에서 재발급 요청
- 이유
  - 클라이언트에서 Access Token의 만료 시점을 별도로 관리하지 않아도 됨
  - 서버 응답을 기준으로 Access Token을 갱신하기 위함<br>
    \* 만료 전 갱신 방식으로 개선 예정

</details>

##  향후 과제
- Access Token 만료 시 WebSocket 인증 갱신
