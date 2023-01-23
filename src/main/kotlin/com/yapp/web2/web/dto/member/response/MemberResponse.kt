package com.yapp.web2.web.dto.member.response

import com.yapp.web2.domain.member.model.Member

data class MemberResponse(
    val memberId: Long,
    val nickname: String,
    val profileImage: String?,
    val jobCategory: String,
    val workingYears: Int,
) {

    companion object {
        fun of(member: Member): MemberResponse {
            return MemberResponse(
                member.id,
                member.nickname,
                member.profileImage,
                member.jobCategory,
                member.workingYears,
            )
        }
    }
}
