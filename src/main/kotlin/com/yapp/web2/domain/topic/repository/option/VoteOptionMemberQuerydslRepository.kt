package com.yapp.web2.domain.topic.repository.option

import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.topic.model.Topic
import com.yapp.web2.domain.topic.model.option.QVoteOptionMember.voteOptionMember
import com.yapp.web2.domain.topic.model.option.VoteOptionMember
import org.springframework.stereotype.Component

@Component
class VoteOptionMemberQuerydslRepository(
    private val queryFactory: JPAQueryFactory,
) {

    fun findByMemberAndTopic(votedBy: Member, votedTopic: Topic): VoteOptionMember? {
        return queryFactory.selectFrom(voteOptionMember)
            .where(voteOptionMember.votedBy.eq(votedBy), voteOptionMember.voteOption.topic.eq(votedTopic))
            .fetchOne()
    }
}
