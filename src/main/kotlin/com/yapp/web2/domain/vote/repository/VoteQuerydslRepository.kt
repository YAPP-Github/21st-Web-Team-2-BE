package com.yapp.web2.domain.vote.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.web2.domain.member.model.QMember.member
import com.yapp.web2.domain.vote.application.vo.LatestVoteSliceVo
import com.yapp.web2.domain.vote.model.QVote.vote
import com.yapp.web2.domain.vote.model.Vote
import org.springframework.stereotype.Component

const val latestVoteSliceSize = 6

@Component
class VoteQuerydslRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findLatestVotes(lastVoteId: Long? = null): LatestVoteSliceVo {
        val results: MutableList<Vote> = queryFactory.select(vote)
            .from(vote)
            .distinct()
            .where(lastVoteId?.let { vote.id.lt(lastVoteId) })
            .orderBy(vote.createdAt.desc())
            .limit((latestVoteSliceSize + 1).toLong())
            .join(vote.createdBy, member).fetchJoin()
            .leftJoin(vote.voteOptions).fetchJoin()
            .fetch()

        return LatestVoteSliceVo(results, hasNext(results))
    }

    private fun hasNext(results: MutableList<Vote>): Boolean {
        return if (results.size > latestVoteSliceSize) {
            results.removeAt(latestVoteSliceSize)
            true
        } else {
            false
        }
    }
}
