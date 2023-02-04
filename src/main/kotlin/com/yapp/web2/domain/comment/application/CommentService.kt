package com.yapp.web2.domain.comment.application

import com.yapp.web2.common.util.findByIdOrThrow
import com.yapp.web2.domain.comment.model.Comment
import com.yapp.web2.domain.comment.respository.CommentQuerydslRepository
import com.yapp.web2.domain.comment.respository.CommentRepository
import com.yapp.web2.domain.like.model.CommentLikes
import com.yapp.web2.domain.like.repository.CommentLikesRepository
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.topic.repository.TopicRepository
import com.yapp.web2.web.dto.comment.request.CommentLikePostRequest
import com.yapp.web2.web.dto.comment.request.CommentPostRequest
import com.yapp.web2.web.dto.comment.response.CommentDetailResponse
import com.yapp.web2.web.dto.comment.response.CommentLikePostResponse
import com.yapp.web2.web.dto.comment.response.CommentPostResponse
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class CommentService(
    private val topicRepository: TopicRepository,
    private val commentRepository: CommentRepository,
    private val commentQuerydslRepository: CommentQuerydslRepository,
    private val commentLikesRepository: CommentLikesRepository,
) {

    fun getLatestComments(voteId: Long, lastCommentId: Long?): Slice<CommentDetailResponse> {
        val findCommentsSlice = commentQuerydslRepository.findComments(voteId, lastCommentId)

        return SliceImpl(
            findCommentsSlice.content.map { comment ->
                CommentDetailResponse.of(
                    comment,
                    comment.commentLikes.size,
                    false //TODO 좋아요 여부
                )
            },
            findCommentsSlice.pageable,
            findCommentsSlice.hasNext()
        )
    }

    @Transactional
    fun saveComment(member: Member, requestDto: CommentPostRequest): CommentPostResponse {
        val topic = topicRepository.findByIdOrThrow(requestDto.topicId)
        val comment = Comment(member, requestDto.contents, topic)

        val savedComment = commentRepository.save(comment)
        return CommentPostResponse.of(savedComment)
    }

    @Transactional
    fun toggleCommentLikes(member: Member, requestDto: CommentLikePostRequest): CommentLikePostResponse {
        val comment = commentRepository.findByIdOrThrow(requestDto.commentId)
        val commentLikes = commentLikesRepository.findByLikedByAndComment(member, comment)

        return if (commentLikes == null) {
            return likeComment(member, comment)
        } else {
            unlikeComment(commentLikes)
        }
    }

    private fun unlikeComment(commentLikes: CommentLikes): CommentLikePostResponse {
        commentLikesRepository.delete(commentLikes)
        return CommentLikePostResponse(commentLikes.comment.id, false)
    }

    private fun likeComment(member: Member, comment: Comment): CommentLikePostResponse {
        val commentLikes = CommentLikes(member, comment)
        comment.addCommentLikes(commentLikes)

        commentLikesRepository.save(commentLikes)
        return CommentLikePostResponse(commentLikes.comment.id, true)
    }
}
