package com.yapp.web2.domain.comment.respository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.web2.domain.comment.model.Comment
import com.yapp.web2.domain.comment.model.QComment.comment
import com.yapp.web2.domain.like.model.QCommentLikes.commentLikes
import com.yapp.web2.domain.member.model.QMember.member
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Component

const val COMMENT_SLICE_SIZE = 10

@Component
class CommentQuerydslRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findComments(voteId: Long, lastCommentId: Long? = null): Slice<Comment> {
        val results = queryFactory.selectFrom(comment)
            .where(comment.topic.id.eq(voteId), lastCommentId?.let { comment.id.lt(lastCommentId) })
            .orderBy(comment.createdAt.desc())
            .limit((COMMENT_SLICE_SIZE + 1).toLong())
            .join(comment.createdBy, member).fetchJoin()
            .leftJoin(comment.commentLikes, commentLikes).fetchJoin()
            .distinct()
            .fetch()

        return SliceImpl(results, Pageable.ofSize(COMMENT_SLICE_SIZE), hasNext(results))
    }

    private fun hasNext(results: MutableList<Comment>): Boolean {
        return if (results.size > COMMENT_SLICE_SIZE) {
            results.removeAt(COMMENT_SLICE_SIZE)
            true
        } else {
            false
        }
    }
}
