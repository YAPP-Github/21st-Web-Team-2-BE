package com.yapp.web2.domain.topic.repository

import com.querydsl.core.types.ExpressionUtils
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.web2.domain.comment.model.QComment.comment
import com.yapp.web2.domain.like.model.QTopicLikes.*
import com.yapp.web2.domain.topic.model.TopicCategory
import com.yapp.web2.domain.member.model.QMember.member
import com.yapp.web2.domain.topic.application.vo.LatestTopicSliceVo
import com.yapp.web2.domain.topic.application.vo.TopicDetailVo
import com.yapp.web2.domain.topic.application.vo.TopicPreviewVo
import com.yapp.web2.domain.topic.model.QHashTag
import com.yapp.web2.domain.topic.model.QTopic.topic
import com.yapp.web2.domain.topic.model.Topic

import com.yapp.web2.domain.topic.model.option.QVoteOption.voteOption
import com.yapp.web2.domain.topic.model.option.QVoteOptionMember.voteOptionMember
import com.yapp.web2.web.dto.topic.request.TopicSearchRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Component

const val LATEST_VOTE_SLICE_SIZE = 6
const val POPULAR_VOTE_SIZE = 4

@Component
class TopicQuerydslRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findLatestTopicsByCategory(
        lastTopicId: Long? = null,
        topicCategory: TopicCategory? = null
    ): LatestTopicSliceVo {
        val results = queryFactory.select(
            Projections.constructor(
                TopicPreviewVo::class.java,
                topic,
                ExpressionUtils.`as`(commentAmountFindQuery(), "commentAmount"),
                ExpressionUtils.`as`(voteAmountFindQuery(), "voteAmount"),
            )
        )
            .from(topic)
            .where(
                lastTopicId?.let { topic.id.lt(lastTopicId) },
                topicCategory?.let { topic.topicCategory.eq(topicCategory) }
            )
            .orderBy(topic.createdAt.desc())
            .limit((LATEST_VOTE_SLICE_SIZE + 1).toLong())
            .join(topic.createdBy, member).fetchJoin()
//            .join(vote.voteOptions).fetchJoin()  TODO voteOptions fetchJoin에 대한 성능 비교 후 적용 필요
            .distinct()
            .fetch()

        return LatestTopicSliceVo(results, hasNext(results))
    }

    fun findPopularTopics(): MutableList<TopicPreviewVo> {
        val numberPath = Expressions.numberPath(Long::class.java, "voteAmount")

        return queryFactory.select(
            Projections.constructor(
                TopicPreviewVo::class.java,
                topic.`as`("vote"),
                ExpressionUtils.`as`(commentAmountFindQuery(), "commentAmount"),
                ExpressionUtils.`as`(voteAmountFindQuery(), "voteAmount"),
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

    fun searchByTitleAndContentOrTag(
        topicSearchRequest: TopicSearchRequest,
        pageable: Pageable
    ): SliceImpl<TopicPreviewVo> {
        val topics = queryFactory.select(
            Projections.constructor(
                TopicPreviewVo::class.java,
                topic,
                ExpressionUtils.`as`(commentAmountFindQuery(), "commentAmount"),
                ExpressionUtils.`as`(voteAmountFindQuery(), "voteAmount"),
            )
        )
            .from(topic)
            .leftJoin(topic.hashTags, QHashTag.hashTag1)
            .where(
                titleOrContentsLike(topicSearchRequest),
                tagEq(topicSearchRequest)
            )
            .offset(pageable.offset)
            .limit((pageable.pageSize + 1).toLong())
            .orderBy(topic.createdAt.desc())
            .distinct()
            .fetch()

        return SliceImpl(topics, pageable, hasNext(topics))
    }

    fun findTopicDetailById(topicId: Long): TopicDetailVo? {
        return queryFactory.select(
            Projections.constructor(
                TopicDetailVo::class.java,
                topic,
                ExpressionUtils.`as`(commentAmountFindQuery(), "commentAmount"),
                ExpressionUtils.`as`(voteAmountFindQuery(), "voteAmount"),
                ExpressionUtils.`as`(voteLikedAmountFindQuery(), "likedAmount"),
            )
        )
            .distinct()
            .from(topic)
            .where(topic.id.eq(topicId))
            .join(topic.createdBy, member).fetchJoin()
            .leftJoin(topic.voteOptions, voteOption).fetchJoin()
            .fetch()
            .distinct()
            .first()
    }

    // 투표 게시글의 투표 수를 조회하는 서브쿼리
    private fun voteAmountFindQuery(): JPQLQuery<Long> =
        JPAExpressions.select(voteOptionMember.count()).from(voteOptionMember).where(
            voteOptionMember.voteOption.topic.id.eq(topic.id)
        )

    // 투표 게시글의 댓글 수를 조회하는 서브쿼리
    private fun commentAmountFindQuery(): JPQLQuery<Long> =
        JPAExpressions.select(comment.count()).from(comment).where(comment.topic.id.eq(topic.id))

    private fun voteLikedAmountFindQuery(): JPQLQuery<Long> =
        JPAExpressions.select(topicLikes.count()).from(topicLikes).where(
            topicLikes.topic.id.eq(topic.id)
        )

    private fun titleOrContentsLike(topicSearchRequest: TopicSearchRequest) =
        topicSearchRequest.searchQuery?.let {
            topic.title.upper().like("%${topicSearchRequest.searchQuery.uppercase()}%")
                .or(topic.contents.upper().like("%${topicSearchRequest.searchQuery.uppercase()}%"))
        }

    private fun tagEq(topicSearchRequest: TopicSearchRequest) =
        topicSearchRequest.hashTag
            ?.let {
                QHashTag.hashTag1.hashTag.upper().eq(topicSearchRequest.hashTag.uppercase())
            }

    private fun hasNext(results: MutableList<TopicPreviewVo>): Boolean {
        return if (results.size > LATEST_VOTE_SLICE_SIZE) {
            results.removeAt(LATEST_VOTE_SLICE_SIZE)
            true
        } else {
            false
        }
    }
}
