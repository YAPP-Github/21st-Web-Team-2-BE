package com.yapp.web2.web.dto.member.response

import com.yapp.web2.domain.member.model.Member

data class MemberResponse(
    val memberId: Long,
    val nickname: String,
    val profileImage: String?,
    val jobCategory: String,
    val workingYears: String,
) {

    companion object {
        fun of(member: Member): MemberResponse {
            return MemberResponse(
                member.id,
                member.nickname,
                member.profileImage,
                member.jobCategory,
                this.convertIntToString(member.workingYears),
            )
        }

        private fun convertIntToString(years: Int) = when (years) {
            0 -> "1년 미만"
            in 1..9 -> "${years}년차"
            else -> "10년 이상"
        }
    }
}
