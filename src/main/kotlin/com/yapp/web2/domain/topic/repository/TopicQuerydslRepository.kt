package com.yapp.web2.domain.topic.repository

import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.web2.domain.comment.model.QComment.comment
import com.yapp.web2.domain.like.model.QTopicLikes
import com.yapp.web2.domain.like.model.QTopicLikes.*
import com.yapp.web2.domain.member.model.JobCategory
import com.yapp.web2.domain.member.model.QMember.member
import com.yapp.web2.domain.topic.application.vo.LatestTopicSliceVo
import com.yapp.web2.domain.topic.application.vo.TopicDetailVo
import com.yapp.web2.domain.topic.application.vo.TopicPreviewVo
import com.yapp.web2.domain.topic.model.QTopic.topic

import com.yapp.web2.domain.topic.model.option.QVoteOption.voteOption
import com.yapp.web2.domain.topic.model.option.QVoteOptionMember.voteOptionMember
import org.springframework.stereotype.Component

const val LATEST_VOTE_SLICE_SIZE = 6
const val POPULAR_VOTE_SIZE = 4

@Component
class TopicQuerydslRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findLatestTopicsByCategory(lastTopicId: Long? = null, jobCategory: JobCategory? = null): LatestTopicSliceVo {
        val results = queryFactory.select(
            Projections.constructor(
                TopicPreviewVo::class.java,
                topic,
                ExpressionUtils.`as`(commentAmountFindQuery(), "commentAmount"),
                ExpressionUtils.`as`(voteAmountFindQuery(),"voteAmount"),
            )
        )
            .from(topic)
            .where(
                lastTopicId?.let { topic.id.lt(lastTopicId) },
                jobCategory?.let { topic.jobCategory.eq(jobCategory) }
            )
            .orderBy(topic.createdAt.desc())
            .limit((LATEST_VOTE_SLICE_SIZE + 1).toLong())
            .join(topic.createdBy, member).fetchJoin()
//            .join(vote.voteOptions).fetchJoin()  TODO voteOptions fetchJoin??? ?????? ?????? ?????? ??? ?????? ??????
            .distinct()
            .fetch()

        return LatestTopicSliceVo(results, hasNext(results))
    }

    private fun hasNext(results: MutableList<TopicPreviewVo>): Boolean {
        return if (results.size > LATEST_VOTE_SLICE_SIZE) {
            results.removeAt(LATEST_VOTE_SLICE_SIZE)
            true
        } else {
            false
        }
    }

    fun findPopularTopics(): MutableList<TopicPreviewVo> {
        val numberPath = Expressions.numberPath(Long::class.java, "voteAmount")

        return queryFactory.select(
            Projections.constructor(
                TopicPreviewVo::class.java,
                topic.`as`("vote"),
                ExpressionUtils.`as`(commentAmountFindQuery(), "commentAmount"),
                ExpressionUtils.`as`(voteAmountFindQuery(),"voteAmount"),
            )
        )
            .from(topic)
//            .where(vote.createdAt.after(LocalDateTime.now().minusDays(7L)))
            .orderBy(numberPath.desc())
            .limit(POPULAR_VOTE_SIZE.toLong())
            .join(topic.createdBy, member).fetchJoin()
            .distinct()
            .fetch()
    }


    // ?????? ???????????? ?????? ?????? ???????????? ????????????
    private fun voteAmountFindQuery(): JPQLQuery<Long> =
        JPAExpressions.select(voteOptionMember.count()).from(voteOptionMember).where(
            voteOptionMember.voteOption.topic.id.eq(topic.id)
        )

    // ?????? ???????????? ?????? ?????? ???????????? ????????????
    private fun commentAmountFindQuery(): JPQLQuery<Long> =
        JPAExpressions.select(comment.count()).from(comment).where(comment.topic.id.eq(topic.id))

    fun findTopicDetailById(voteId: Long): TopicDetailVo? {
        return queryFactory.select(
            Projections.constructor(
                TopicDetailVo::class.java,
                topic,
                ExpressionUtils.`as`(commentAmountFindQuery(), "commentAmount"),
                ExpressionUtils.`as`(voteAmountFindQuery(),"voteAmount"),
                ExpressionUtils.`as`(voteLikedAmountFindQuery(),"likedAmount"),
            )
        )
            .distinct()
            .from(topic)
            .where(topic.id.eq(voteId))
            .join(topic.createdBy, member).fetchJoin()
            .leftJoin(topic.voteOptions, voteOption).fetchJoin()
            .fetch()
            .distinct()
            .first()
    }

    private fun voteLikedAmountFindQuery(): JPQLQuery<Long> =
        JPAExpressions.select(topicLikes.count()).from(topicLikes).where(
            topicLikes.topic.id.eq(topic.id)
        )


}
