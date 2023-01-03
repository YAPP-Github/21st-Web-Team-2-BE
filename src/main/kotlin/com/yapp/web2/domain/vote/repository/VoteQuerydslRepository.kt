package com.yapp.web2.domain.vote.repository

import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.web2.domain.comment.model.QComment.comment
import com.yapp.web2.domain.member.model.QMember.member
import com.yapp.web2.domain.vote.application.vo.LatestVoteSliceVo
import com.yapp.web2.domain.vote.application.vo.VotePreviewVo
import com.yapp.web2.domain.vote.model.QVote.vote
import com.yapp.web2.domain.vote.model.option.QVoteOptionMember.voteOptionMember
import org.springframework.stereotype.Component
import java.time.LocalDateTime

const val latestVoteSliceSize = 6
const val popularVoteSize = 4

@Component
class VoteQuerydslRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findLatestVotes(lastVoteId: Long? = null): LatestVoteSliceVo {
        val results = queryFactory.select(
            Projections.constructor(
                VotePreviewVo::class.java,
                vote,
                ExpressionUtils.`as`(commentAmountFindQuery(), "commentAmount"),
                ExpressionUtils.`as`(voteAmountFindQuery(),"voteAmount"),
            )
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

    fun findPopularVotes(): MutableList<VotePreviewVo> {
        val numberPath = Expressions.numberPath(Long::class.java, "voteAmount")

        val results = queryFactory.select(
            Projections.constructor(
                VotePreviewVo::class.java,
                vote.`as`("vote"),
                ExpressionUtils.`as`(commentAmountFindQuery(), "commentAmount"),
                ExpressionUtils.`as`(voteAmountFindQuery(),"voteAmount"),
            )
        ).from(vote)
//            .where(vote.createdAt.after(LocalDateTime.now().minusDays(7L)))
            .orderBy(numberPath.desc())
            .limit(popularVoteSize.toLong())
            .join(vote.createdBy, member).fetchJoin()
            .distinct()
            .fetch()

        return results
    }


    private fun voteAmountFindQuery(): JPQLQuery<Long> =
        JPAExpressions.select(voteOptionMember.count()).from(voteOptionMember).where(
            voteOptionMember.voteOption.vote.id.eq(vote.id)
        )

    private fun commentAmountFindQuery(): JPQLQuery<Long> =
        JPAExpressions.select(comment.count()).from(comment).where(comment.vote.id.eq(vote.id))


}
