package com.yapp.web2.common

import com.yapp.web2.domain.member.model.Member

class EntityFactory {
    companion object {
        fun testMemberA() = Member(
            nickname = "MemberA",
            email = "MemberA@test.com",
            jobCategory = "developer",
            workingYears = 3
        )

        fun testMemberB() = Member(
            nickname = "MemberB",
            email = "MemberB@test.com",
            jobCategory = "Designer",
            workingYears = 5
        )

        fun testMemberC() = Member(
            nickname = "MemberC",
            email = "MemberC@test.com",
            jobCategory = "product_manager",
            workingYears = 1
        )
    }
}
