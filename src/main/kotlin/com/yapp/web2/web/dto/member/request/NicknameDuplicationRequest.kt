package com.yapp.web2.web.dto.member.request

data class NicknameDuplicationRequest(
    val nickname: String
) {
    fun isValid(): Boolean {
        return nickname.length <= NICKNAME_MAX_LENGTH
    }

    companion object {
        const val NICKNAME_MAX_LENGTH = 20
    }
}
