package com.yapp.web2.web.dto.member.response

import com.yapp.web2.domain.member.model.JobCategory
import com.yapp.web2.domain.member.model.Member

data class MemberResponse(
    val id: Long,
    val name: String,
    val profileImage: String?,
    val jobCategory: JobCategory,
    val workingYears: Int,
) {

    companion object {
        fun of(member: Member): MemberResponse {
            return MemberResponse(
                member.id,
                member.nickname,
                member.profileImageFilename,
                member.jobCategory,
                member.workingYears,
            )
        }
    }
}
