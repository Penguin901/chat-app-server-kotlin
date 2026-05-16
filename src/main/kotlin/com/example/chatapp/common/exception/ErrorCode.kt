package com.example.chatapp.common.exception

import org.springframework.http.HttpStatus

// TODO: 서버는 클라이언트에 에러 코드만 전달하고, 메시지는 클라이언트에서 처리하도록 변경할 것
enum class ErrorCode(
    val httpStatus: HttpStatus,
    val message: String
) {
    // OAUTH 인증 실패 / 토큰 오류 (Authentication) - 401
    INVALID_OAUTH_TOKEN(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다."),
    OAUTH_SERVER_ERROR(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다."),

    // Auth (서버 인증 실패)
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 요청입니다."),
    INVALID_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "유효하지 않은 요청입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    OAUTH_PROVIDER_MISMATCH(HttpStatus.CONFLICT, "다른 간편로그인 계정으로 로그인해 주세요."),

    // 인증이 아예 안 된 경우 - 로그인 안 한 경우
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),

    // 인가 실패 (Authorization) - 403
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없는 사용자입니다."),

    // UserException
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."), //404
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용중인 아이디 입니다."), // 409
    INVALID_USERNAME(HttpStatus.BAD_REQUEST, "아이디는 4~20자 이어야 합니다."),
    INVALID_SEARCH_KEYWORD(HttpStatus.BAD_REQUEST, "검색어는 4자 이상이어야 합니다"),

    // FriendException
    FRIEND_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 친구로 등록된 사용자 입니다."),
    SELF_FRIEND_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "유효하지 않은 요청입니다."),

    // ChatRoomException
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "유효하지 않은 요청입니다."),
    CANNOT_ADD_SELF_AS_PARTICIPANT(HttpStatus.BAD_REQUEST, "유효하지 않은 요청입니다."),
    NOT_A_MEMBER(HttpStatus.FORBIDDEN, "유효하지 않은 요청입니다."),
    DUPLICATE_PARTICIPANTS(HttpStatus.CONFLICT, "유효하지 않은 요청입니다."),

    // 요청 값 / 제약 위반 (Validation, DB) - 400
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "오류가 발생했습니다."),

    // 서버 내부 오류 - 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "오류가 발생했습니다.");


}