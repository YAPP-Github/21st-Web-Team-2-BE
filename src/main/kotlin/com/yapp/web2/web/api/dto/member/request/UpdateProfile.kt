package com.yapp.web2.web.api.dto.member.request

data class UpdateProfile(
    val jobCategory: String,
    val workingYears: Int,
    val nickname: String,
)
