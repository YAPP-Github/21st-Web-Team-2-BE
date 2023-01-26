package com.yapp.web2.web.api.error

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String,
) {
    //Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "1000", "서버 내부 오류가 발생하였습니다."),

    //Auth
    OAUTH2_FAIL_EXCEPTION(HttpStatus.UNAUTHORIZED, "2000", "유효하지 않는 Oauth2 엑세스 토큰입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "2001", "존재하지 않는 회원입니다."),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "2002", "만료된 엑세스 토큰입니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "2003", "유효하지 않는 엑세스 토큰입니다."),
    NULL_JWT(HttpStatus.UNAUTHORIZED, "2003", "엑세스 토큰이 필요합니다."),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "2004", "닉네임 형식이 옳지 않습니다"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "2005", "만료된 리프레쉬 토큰입니다."),

    // Data base
    NOT_FOUND_DATA(HttpStatus.NOT_FOUND, "4000", "존재하지 않는 리소스 요청입니다."),
    NULL_VALUE(HttpStatus.BAD_REQUEST, "4001", "필수값이 포함되지 않았습니다."),
}
