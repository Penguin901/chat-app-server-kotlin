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
>서버 운영상황에 따라 페이지가 작동하지 않을 수 있습니다.

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

### 소셜 로그인 시 인증 책임을 클라이언트와 서버 중 어느 곳에 둘 것인가?
- 구현 방식
    - A: 클라이언트에서 OIDC(OpenID Connect) 인증 후 ID Token 획득
    - B: 서버에서  OIDC(OpenID Connect) 인증 후 ID Token 획득

- 선택
    - A: 클라이언트에서 ID Token 획득 후 서버로 전송, 서버에서 검증 후 JWT 발급
- 이유
    - 모바일 환경에 적합한 사용자 경험 제공
    - 서버는 ID Token 검증과 자체 JWT 발급에만 집중하도록 함

### 채팅방을 언제 생성할 것인가?
- 구현 방식
    - A: HTTP 요청으로 채팅방 생성 후, WebSocket 요청으로 메시지 전송
    - B: 채팅방 생성과 메시지 전송을 WebSocket 요청에서 처리<br>
      \* 사용자 경험은 동일함
- 선택
    - A: HTTP 요청으로 채팅방 생성 후, WebSocket 요청으로 메시지 전송
- 이유
    - 채팅방 생성과 메시지 전송 요청 분리
    - 메시지 처리 로직 단순화
### 채팅방 퇴장 시 사용자의 참여 상태를 어떻게 관리할 것인가?
- 구현 방식
    - A: 퇴장한 사용자의 참여 상태만 변경 (Soft Delete)
    - B: 퇴장한 사용자를 채팅방 멤버 목록에서 삭제 (Hard Delete)
- 선택
    - A: 퇴장한 사용자의 참여 상태만 변경 (Soft Delete)
- 이유
    -  채팅방 멤버 목록에서 삭제(Hard Delete) 할 경우 동일 사용자 간 채팅방과 메시지의 중복 생성 문제가 있음
### Access Token 만료를 어떻게 감지하고 갱신할 것인가?
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

## 문제 발생 및 해결 과정
<details>
<summary>보기</summary>

### 문제 상황:  클라이언트(Flutter 앱)와 서버 간 End To End 테스트 중 채팅방 나가기 실패

- 시퀀스 다이어그램
 ```mermaid
sequenceDiagram

    actor U1 as 사용자 A
    actor U2 as 사용자 B

    participant Server as 서버
    U1->>Server: 사용자 B와의 채팅방 생성 및 메시지 전송
    U1->>Server: 채팅방 나가기

    U2->>Server: 메시지 전송 (사용자 A 재입장)

    U2->>Server: 채팅방 나가기

    U1->>Server: 채팅방 나가기
    Server-->>U1: Error Code: NOT_A_MEMBER
 ```
### 원인 분석

#### 1. 서버에서 Error Code: NOT_A_MEMBER 응답한 이유
- 채팅방 삭제 시 해당 방의 멤버인지 확인 후 삭제하는데, 사용자 A의 나가기 요청 전 DB에서 채팅방이 삭제되었음
- 사용자 A는 존재하지 않는 채팅방에 나가기 요청을 하게 되어 예외 발생, 서버에서 NOT_A_MEMBER 응답<br>

&nbsp;&nbsp;&nbsp;※ 서버에서 채팅방이 이미 삭제되었으나 사용자 A의 나가기 요청이 가능했던 이유<br>
&nbsp;&nbsp;&nbsp;&nbsp;-> 클라이언트는 자신의 요청이 성공한 경우에만 로컬 DB를 업데이트하며, 로컬 DB에서 채팅방 목록을 조회함.<br>&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 따라서 사용자 B의 나가기 요청으로 서버에서 삭제된 채팅방은 사용자 B의 로컬 DB에만 반영되었고,<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 사용자 A의 로컬 DB에는 반영되지 않아 사용자 A의 채팅방 목록에 기존 채팅방이 그대로 남아 있었음

#### 2. 사용자 A 재입장 후 나가기 요청 전 채팅방이 삭제된 이유
- 채팅방은 방의 모든 멤버가 비활성 상태일 때에만 삭제 가능 (입장 시 활성멤버, 퇴장 시 비활성멤버로 변경)
- 사용자 A 재입장 시 방의 멤버인 A의 상태가 비활성(active = false) -> 활성(active = true)으로 변경되지 않음
- 사용자 B의 나가기 요청 후 모든 멤버가 비활성 상태이므로 방이 삭제됨<br>

  <확인><br>
    - 디버깅 -> 사용자 A 재입장 시 A의 상태가 활성화되는지 확인<br><br>
      &nbsp;&nbsp;&nbsp;&nbsp;<img width="666" height="572" alt="Untitled Diagram drawio-4" src="https://github.com/user-attachments/assets/99d9f154-e6ea-4b19-8f98-99f325405163" /><br>
    - 디버깅 결과<br><br>
      &nbsp;&nbsp;&nbsp;&nbsp;<img width="658" height="150" alt="Screenshot 2026-06-20 at 10 50 00 PM" src="https://github.com/user-attachments/assets/cab4c120-2dad-4f28-8b2b-8ffd73667a5b" /><br>
    - DB에 저장된 값: active = false

    - 결론<br>
        - 디버깅 결과(active = true)와 DB에 저장된 값(active = false) 불일치<br>
          -> 채팅방 멤버 활성화 로직은 정상 동작, DB 업데이트 과정에 문제 있음

#### 3. 사용자 A 재입장 시 활성상태로 DB가 업데이트되지 않은 이유
- Spring Data JPA의 Dirty Checking으로 DB를 업데이트 하도록 구현하였으나,<br>
  &nbsp;@Transactional 선언을 하지 않아 UPDATE 쿼리가 실행되지 않았고, DB에 반영되지 않았음<br>

  <확인><br>
    - logging.level.org.hibernate.SQL=DEBUG 설정을 통해 SQL 로그 출력<br>
      -> 채팅방의 멤버 상태를 변경하는 UPDATE 쿼리가 출력되지 않음

### 해결
- activateInactiveMembers를 호출하는 클래스(ChatMessageUseCase)에 @Transactional 선언 추가

### 배운점
- 영속성 컨텍스트의 생명주기와 트랜잭션의 관계
- 테스트 코드의 중요성
    - 사전에 서버에서 테스트해 봤으면 클라이언트와의 End To End 테스트 전에 문제 발견할 수 있었음
</details>

##  향후 과제
- Access Token 만료 시 WebSocket 인증 갱신
- 성능 최적화 방안 고민
