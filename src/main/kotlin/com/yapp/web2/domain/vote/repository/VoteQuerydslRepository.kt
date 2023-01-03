package com.yapp.web2.domain.vote.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.web2.domain.comment.model.QComment.comment
import com.yapp.web2.domain.member.model.QMember.member
import com.yapp.web2.domain.vote.application.vo.LatestVoteSliceVo
import com.yapp.web2.domain.vote.application.vo.VotePreviewVo
import com.yapp.web2.domain.vote.model.QVote.vote
import com.yapp.web2.domain.vote.model.option.QVoteOptionMember.voteOptionMember
import org.springframework.stereotype.Component

const val latestVoteSliceSize = 6

@Component
class VoteQuerydslRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findLatestVotes(lastVoteId: Long? = null): LatestVoteSliceVo {
        val commentAmountSubQuery =
            JPAExpressions.select(comment.count()).from(comment).where(comment.vote.id.eq(vote.id))

        val voteAmountSubQuery = JPAExpressions.select(voteOptionMember.count()).from(voteOptionMember).where(
            voteOptionMember.voteOption.vote.id.eq(vote.id)
        )
        val results = queryFactory.select(
            Projections.constructor(
                VotePreviewVo::class.java,
                vote,
                commentAmountSubQuery,
                voteAmountSubQuery)
            ).from(vote)
            .where(lastVoteId?.let { vote.id.lt(lastVoteId) })
            .orderBy(vote.createdAt.desc())
            .limit((latestVoteSliceSize + 1).toLong())
            .join(vote.createdBy, member).fetchJoin()
//            .join(vote.voteOptions).fetchJoin()
            .distinct()
            .fetch()

        return LatestVoteSliceVo(results, hasNext(results))
    }

    private fun hasNext(results: MutableList<VotePreviewVo>): Boolean {
        return if (results.size > latestVoteSliceSize) {
            results.removeAt(latestVoteSliceSize)
            true
        } else {
            false
        }
    }
}
