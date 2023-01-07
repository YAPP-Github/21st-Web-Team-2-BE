package com.yapp.web2.web.api.error

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val code: String,
    val message: String,
) {
    //Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "1000", "서버 내부 오류가 발생하였습니다."),

    // Data base
    NOT_FOUND_DATA(HttpStatus.NOT_FOUND, "4000", "존재하지 않는 리소스 요청입니다."),
}
