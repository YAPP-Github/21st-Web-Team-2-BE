package com.yapp.web2.common

import com.yapp.web2.domain.member.model.Member
import org.springframework.stereotype.Component

@Component
class EntityFactory {
    companion object {
        fun testMemberA() = Member(
            id = 1L,
            nickname = "MemberA",
            email = "MemberA@test.com",
            jobCategory = "developer",
            workingYears = 3
        )

        fun testMemberB() = Member(
            id = 2L,
            nickname = "MemberB",
            email = "MemberB@test.com",
            jobCategory = "Designer",
            workingYears = 5
        )

        fun testMemberC() = Member(
            id = 3L,
            nickname = "MemberC",
            email = "MemberC@test.com",
            jobCategory = "product_manager",
            workingYears = 1
        )
    }
}
