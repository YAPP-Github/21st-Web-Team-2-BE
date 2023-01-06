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
import com.yapp.web2.domain.vote.model.Vote
import com.yapp.web2.domain.vote.model.option.QVoteOption.voteOption
import com.yapp.web2.domain.vote.model.option.QVoteOptionMember.voteOptionMember
import org.springframework.stereotype.Component

const val LATEST_VOTE_SLICE_SIZE = 6
const val POPULAR_VOTE_SIZE = 4

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
        )
            .from(vote)
            .where(lastVoteId?.let { vote.id.lt(lastVoteId) })
            .orderBy(vote.createdAt.desc())
            .limit((LATEST_VOTE_SLICE_SIZE + 1).toLong())
            .join(vote.createdBy, member).fetchJoin()
//            .join(vote.voteOptions).fetchJoin()  TODO voteOptions fetchJoin에 대한 성능 비교 후 적용 필요
            .distinct()
            .fetch()

        return LatestVoteSliceVo(results, hasNext(results))
    }

    private fun hasNext(results: MutableList<VotePreviewVo>): Boolean {
        return if (results.size > LATEST_VOTE_SLICE_SIZE) {
            results.removeAt(LATEST_VOTE_SLICE_SIZE)
            true
        } else {
            false
        }
    }

    fun findPopularVotes(): MutableList<VotePreviewVo> {
        val numberPath = Expressions.numberPath(Long::class.java, "voteAmount")

        return queryFactory.select(
            Projections.constructor(
                VotePreviewVo::class.java,
                vote.`as`("vote"),
                ExpressionUtils.`as`(commentAmountFindQuery(), "commentAmount"),
                ExpressionUtils.`as`(voteAmountFindQuery(),"voteAmount"),
            )
        )
            .from(vote)
//            .where(vote.createdAt.after(LocalDateTime.now().minusDays(7L)))
            .orderBy(numberPath.desc())
            .limit(POPULAR_VOTE_SIZE.toLong())
            .join(vote.createdBy, member).fetchJoin()
            .distinct()
            .fetch()
    }


    // 투표 게시글의 투표 수를 조회하는 서브쿼리
    private fun voteAmountFindQuery(): JPQLQuery<Long> =
        JPAExpressions.select(voteOptionMember.count()).from(voteOptionMember).where(
            voteOptionMember.voteOption.vote.id.eq(vote.id)
        )

    // 투표 게시글의 댓글 수를 조회하는 서브쿼리
    private fun commentAmountFindQuery(): JPQLQuery<Long> =
        JPAExpressions.select(comment.count()).from(comment).where(comment.vote.id.eq(vote.id))

    fun findVoteById(voteId: Long): Vote? {
        return queryFactory.selectFrom(vote)
            .where(vote.id.eq(voteId))
            .join(vote.createdBy, member).fetchJoin()
            .leftJoin(vote.voteOptions, voteOption).fetchJoin()
            .distinct()
            .fetchOne()
    }


}
