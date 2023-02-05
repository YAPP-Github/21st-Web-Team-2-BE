package com.yapp.web2.common

import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.topic.model.Topic
import com.yapp.web2.domain.topic.model.TopicCategory
import com.yapp.web2.domain.topic.model.VoteType
import com.yapp.web2.domain.topic.model.option.VoteOption

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

        fun testTopicA(createdBy: Member): Topic {
            val topic = Topic(
                "VoteA",
                TopicCategory.DEVELOPER,
                "ContentA",
                VoteType.TEXT,
                createdBy = createdBy,
            )

            topic.addVoteOption(VoteOption("${topic.contents} OptionA", null, null, null, topic))
            topic.addVoteOption(VoteOption("${topic.contents} OptionB", null, null, null, topic))

            return topic
        }
    }
}
