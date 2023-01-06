package com.yapp.web2.domain.comment.application

import com.yapp.web2.domain.comment.respository.CommentQuerydslRepository
import com.yapp.web2.web.dto.comment.response.CommentDetailResponse
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class CommentService(
    private val commentQuerydslRepository: CommentQuerydslRepository,
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
}
