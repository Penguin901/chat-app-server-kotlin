package com.example.chatapp.common.exception

open class BusinessException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)

class AuthException(errorCode: ErrorCode) : BusinessException(errorCode)
class UserException(errorCode: ErrorCode) : BusinessException(errorCode)
class FriendException(errorCode: ErrorCode) : BusinessException(errorCode)
class ChatRoomException(errorCode: ErrorCode) : BusinessException(errorCode)

