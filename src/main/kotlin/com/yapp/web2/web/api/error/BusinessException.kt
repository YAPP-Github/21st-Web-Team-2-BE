package com.yapp.web2.web.api.error

class BusinessException(
    val errorCode: ErrorCode
) : RuntimeException()
