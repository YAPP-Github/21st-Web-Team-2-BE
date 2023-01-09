package com.yapp.web2.web.dto.auth.request

data class SignUpRequest(
    val nickname: String,
    val jobCategory: String,
    val workingYears: Int
)
