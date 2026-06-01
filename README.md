# Chat Application Server Kotlin

# 제목(메인레포 링크이름과 동일하게)

## 소개

자바는 코드가 길어지고 ~ 앞서 자바와 스프링부트로 구현하였던 서버를 코틀린으로 마이그레이션 하였습니다. 전체적으로 ~

## 주요 기능

<details>
<summary>
   기능별 비즈니스 로직
</summary>


Authentication  
 - OIDC(OpenID Connect)기반의 소셜로그인을 지원한다.
 - 사용자는 계정 생성에 사용한 OpenID 공급자를 통해서만 로그인할 수 있다.
 - OpenID 공급자가 발급한 ID Token을 검증한 후, 서비스에서 사용할 사용자 계정 생성 및 사용자 인증을 위한 JSON Web Token(Access Token, Refresh Token)을 발급한다.
 - 인증이 필요한 요청은 Access Token을 통해 사용자를 검증한다.
 - Access Token이 만료된 경우 Refresh Token으로 사용자 검증 후 Access Token, Refresh Token을 재발급한다.

User
 - 사용자는 서비스를 이용하기 위해 로그인을 해야한다.
 - 사용자는 자신의 정보(프로필정보, 계정아이디)를 수정할 수 있다.
 - 회원탈퇴시 사용자를 비활성화 처리한다.

Friend
 - 친구추가전 사용자를 검색할 수 있으며, 이미 친구 관계가 생성된 사용자는 검색결과에서 제외한다.
 - 친구추가시 사용자간 단방향 친구관계를 생성한다.


Chat 
 - 1대1 채팅의 경우 사용자간 고유한 키를 생성하여 중복된 채팅방이 생성되지 않도록 한다.
 - 사용자가 메세지를 전송하는 시점에 채팅방을 생성한후 메세지를 전송하며, 기존 채팅방이 존재하는 경우 해당방으로 메세지를 전송한다.
 - 사용자가 채팅방에서 퇴장한 경우 해당 사용자를 채팅방 멤버에서 비활성화한다. 
 - 채팅방의 활성 멤버가 1명 이상인 경우 방을 유지하며, 모든 멤버가 비활성 상태인 경우 채팅방과 관련된 모든 데이터를 삭제한다. 
 - 퇴장한 사용자가 재입장하는 경우, 또는 상대방이 기존 방으로 메세지를 보낸 경우 퇴장한 사용자를 채팅방 멤버에서 활성화한다.
 - 사용자는 자신이 채팅방의 멤버로 활성화된 시간 이후의 메세지만 확인할 수 있다.
</details>

Authentication
  - 사용자 인증 토큰(JSON Web Token) 발급 및 검증
### User
- 회원가입/로그인
    - 소셜 로그인(OIDC(OpenID Connect))을 통한 회원가입/로그인
    - 로그인 시 사용자 인증을 위한 토큰(JSON Web Token) 발급
- 회원탈퇴
    - 회원탈퇴 시 사용자 비활성 
- 회원정보 수정
    - 프로필 수정
    - 계정 아이디 수정

Friend 
 - 친구 추가할 사용자 검색
 - 친구 추가/삭제

 Chat 
 - 채팅방 생성/삭제
 - 채팅방 입장/퇴장
 - 실시간 메세지 전송




프로젝트의 핵심 기능을 강조하고 웹의 경우 서버 배포까지 했으면 url을, 아닌 경우 사진이나 그림으로 설명한다.
기여도와 역할

## 개선사항.

 - 널 세이프티로 인해 널이되면 안 되는곳은 처음부터 막음
 - 코드개선 (방만 구독으로 변경)
 - 웹소켓 예외처리 추가
 - 웹소켓 인증 개선

## 사용 기술 
 
Kotlin 1.9.x,
Spring Boot 3.2.x, Spring Data JPA (Hibernate), Spring Security, 
WebSocket(STOMP), OAuth 2.0,
MariaDB 10.11.x

## API

### Authentication
| 기능 | Method | URL | 설명 |
|---|---|---|---|
| 로그인 | POST | /auth/login | 소셜로그인방식으로 로그인 후 사용자 계정을 생성하고, 사용자 인증을 위한 JSON Web Token(Access Token, Refresh Token)을 발급합니다.|
| 토큰 재발급 | POST | /auth/refresh | Access Token이 만료된 경우, Refresh Token을 통해 사용자 인증을 위한 JSON Web Token(Access Token, Refresh Token)을 재발급합니다.|
 
| 기능 | Method | URL | 설명 |
|---|---|---|---|
| 내 프로필 조회 | GET | /users/me/profile | 현재 로그인한 사용자의 프로필(닉네임, 상태메세지, 프로필이미지URL)을 조회합니다.|
| 사용자 프로필 조회 | GET | /users/{userId}/profile | 주어진 아이디(userId)의 사용자 프로필(닉네임, 상태메세지, 프로필이미지URL)을 반환합니다.|
| 계정아이디(username) 사용 가능 여부 조회 | GET | /users/username/availability | 유효한 계정아이디(username)인지 검증하고, 중복 여부를 확인합니다. |
| 내 프로필 수정 | PUT |  /users/me/profile | 현재 로그인한 사용자의 프로필정보를 수정합니다. |
| 내 계정아이디(username) 수정| PUT | /users/me/username | 현재 로그인한 사용자의 계정아이디(username)를 수정합니다. |
| 회원탈퇴 | DELETE | /users/me | 현재 로그인한 사용자의 계정을 비활성화합니다. |

## Friend API
| 기능 | Method | URL | 설명 |
|---|---|---|---|
| 친구 목록 조회 | GET | /friends | 현재 로그인한 사용자의 친구 목록을 조회합니다 |
| 친구로 등록할 사용자 조회 | GET | /friends/search | 친구로 추가할 사용자의 존재 및 사용자 간 친구 관계 여부를 확인합니다.|
| 친구 추가 | POST | /friends | 현재 로그인한 사용자와 다른 사용자(targetUserId)의 친구관계를 추가합니다. |
| 친구 삭제 | DELETE | /friends/{targetUserId} | 현재 로그인한 사용자와 다른 사용자(targetUserId)의 친구관계를 삭제합니다. |

 
## Chat Room API

| 기능 | Method | URL | 설명 |
|---|---|---|---|
| 채팅방 목록 조회 | GET | /chat-rooms | 현재 로그인한 사용자의 채팅방 목록을 조회합니다.|
| 채팅방 조회 및 생성 | POST | /chat-rooms | 현재 로그인한 사용자의 채팅방이 존재하는 경우 해당 방을 반환하고, 존재하지 않는 경우 새로운 방을 생성하여 반환합니다. |
| 채팅방 나가기 | DELETE | /chat-rooms/{chatRoomId}/members/me | 주어진 아이디(chatRoomId)의 채팅방을 현재 로그인한 사용자의 채팅방 목록에서 삭제합니다. |


## Chat Message API
| 기능 | Method | URL | 설명 |
|---|---|---|---|
| 메세지 조회 | GET | /chat-messages/{chatRoomId} | 주어진 아이디(chatRoomId)의 메세지를 조회합니다.(클라이언트와의 메세지 데이터 동기화 시 사용) |
| 채팅 메시지 전송 | STOMP | `/pub/message` | 채팅 메시지를 전송합니다. |
| 채팅 메시지 구독 | SUBSCRIBE | `/sub/chatroom/{chatRoomId}` | 특정 채팅방(chatRoomId)의 메시지를 실시간으로 구독합니다. |


erd

결과 및 성과

프로젝트의 결과물과 어떤 문제를 해결했는지, 어떤 성과를 달성했는지 강조한다. 정량적 수치가 있으면 더 좋다.
실제운영상황에서 사용자 증가 고려해 다른 기술추가하면..? 카프카, 레디스 등  직접 기술추가ㅡㄹ ㄹ언급하지는 말고 이런 점을 개선을 위해 생각해보면 좋겠다...라고
 
그외
비슷한 널 세이프티를 가진 dart와 비교해서 왜..비슷한 성격의 언어인데..프레임워크에서의 차이점...
회원탈퇴 하드 딜레이트..하면 다 건드려야..
향후 계획, 프로젝트를 통해 배운점, 프로젝트에서 문제점을 해결한 과정, 프로젝트의 개선점, 확장성등을 이야기 해도 된다.
