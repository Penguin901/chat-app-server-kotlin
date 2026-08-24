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

## 개선/문제해결 사례

<details>
<summary>보기</summary>

### 1. 멀티 스레드 환경에서 1:1 채팅방 생성 시 발생한 동시성 문제 해결 사례
- 문제
  - JUnit에서 두 개의 스레드를 이용한 동시성 테스트 중,<br>
    사용자 A와 B가 동시에 상대방과의 1:1 채팅방 생성 시 DataIntegrityViolationException 발생

- 분석
  - 1:1 채팅방의 중복 생성을 방지하기 위해 두 사용자의 아이디를 조합한 directRoomKey를 생성하고 해당 컬럼에 UNIQUE 제약조건을 적용했었음
  - 채팅방 생성전 기존 채팅방의 존재 여부 조회 시에는 두 사용자 간의 채팅방이 존재하지 않았으므로, 두 스레드는 동일한 directRoomKey로 채팅방 생성 시도
  - 먼저 실행된 스레드 1이 채팅방 생성에 성공하고, 이후 스레드 2에서 생성을 시도했으나 스레드 1이 생성한 채팅방으로 인해 예외 발생

- 해결 과정
  - DataIntegrityViolationException 발생 시 다른 스레드가 방을 생성한 것으로 판단하고, 기존 채팅방을 다시 조회하여 반환하도록 함
  - 기존 코드는 채팅방 생성과 조회가 동일 트랜잭션에 있었음. 따라서 생성 작업에서 발생한 예외가 재조회 시에도 영향을 미치므로,<br>
    이를 해결하기 위해 아래와 같이 전체 로직 변경<br>

    &nbsp;&nbsp;(1) Usecase(채팅방 생성 함수를 호출하는 클래스)에 있던 @Transactional 제거<br>
    &nbsp;&nbsp;(2) Usecase에서 채팅방 조회 수행<br>
    &nbsp;&nbsp;(3) 채팅방 생성 함수에만 @Transactional 선언하여 채팅방 생성 시 발생한 예외가 채팅방 재조회 시 영향을 미치지 않도록 함

- 코드
  커밋후 이미지 첨부
  유스케이스,. 서비스

- 결과
  - 채팅방 생성의 원자성을 보장하면서, 멀티 스레드 환경에서 UNIQUE 제약조건 위반 시
    먼저 생성된 채팅방을 재조회하여 반환할 수 있게 되었음

### 2. 메시지 전송 부하 테스트 중 DB 데드락 문제 해결 사례

- 문제
  - k6를 이용해 배포한 서버를 대상으로 메시지 전송 부하 테스트를 진행하던 중, 일부 메시지
    전송 실패 및 DB에서 데드락 발생

- 원인 분석
  - 클라이언트로부터 수신한 메시지를 WebSocket STOMP를 통해 브로드캐스트 하기 전, ChatMessages 테이블에 메시지를 insert 해야 함
  - ChatMessages 테이블에 insert 하기 위해서는 외래키인 chatRoomId 참조 필요.
    이때 chatRoomId의 외래키 제약조건을 검사하는 과정에서 두 개의 트랜잭션이 각각 S lock을 획득함
  - 이후 ChatRooms 테이블의 lastMessage 컬럼을 update 하기 위해  X Lock 필요.
    두 트랜잭션 모두 S lock을 소유함과 동시에 X lock을 대기하고 있어 데드락 발생

- 해결 과정
  - 현재 코드에는 ChatRoom 객체를 생성하기 위해 채팅방 조회하는 코드가 있음
  - 해당 코드가 ChatMessages 테이블에 메시지를 insert 하는 코드보다 선행하므로, 조회 시점에 X Lock 을 획득하여 트랜잭션 종료 시까지 다른 트랜잭션의 접근을 차단함
  - 조회시점에 X Lock 을 획득하기 위해 채팅방 조회 함수에 비관적락(PESSIMISTIC_WRITE) 적용

- 실제 코드

  커밋후 캡쳐한 이미지 첨부   chatRoomService.findChatRoomForUpdate 가 호출하는 레포지토리 함수
  <img width="659" height="385" alt="Screenshot 2026-08-24 at 3 20 20 PM" src="https://github.com/user-attachments/assets/57efeeb2-6289-4044-b55c-b85edcc531e2" />
  메세지 전송 함수


- 결과
  - 동일 조건으로 부하 테스트를 다시 수행한 결과 데드락이 발생하지 않았음. 또한, 메시지 전
    송이 모두 성공하였고,  평균 응답시간도 70.27ms -> 14.05ms로 개선되었음

</details>

##  향후 과제
- Access Token 만료 시 WebSocket 인증 갱신
- 성능 최적화 방안 고민
