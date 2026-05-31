# Chat Application Server Kotlin

# 제목(메인레포 링크이름과 동일하게)

## 소개

자바는 코드가 길어지고 ~ 앞서 자바와 스프링부트로 구현하였던 서버를 코틀린으로 마이그레이션 하였습니다. 전체적으로 ~

## 주요 기능

# 로그인
- OAuth 2.0 기반 구글 로그인을 지원합니다.
- 구글의 ID Token을 검증한 후 사용자 계정을 생성합니다.

사용자 인증  
- JSON Web Token(Access Token, Refresh Token) 인증을 사용합니다.
- Access Token을 통해 유효한 사용자인지 검증합니다.
- Access Token만료시 Refresh Token을 통해 재발급합니다.

사용자  = 
= 
 = 프로필 정보 수정(닉네임, 상태메세지, 프로필 사진)
 - 계정 정보 수정(유저네임)
 - 회원탈퇴 시 사용자의 delete 상태를 true로 변경합니다.

- 친구
 - 친구로 등록할 사용자를 조회 할 수 있습니다.
 - 다른 사용자를 친구로 등록하거나 친구목록에서 삭제할 수 있습니다.

 
- 채팅
  - WebSocket을 통한 실시간 채팅을 지원합니다.
  - 친구로 등록한 사용자에게 메세지를 보낼 수 있습니다.
  - 동일 사용자와 채팅이 존재하는 경우 기존채팅을 사용


 
  - 

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
