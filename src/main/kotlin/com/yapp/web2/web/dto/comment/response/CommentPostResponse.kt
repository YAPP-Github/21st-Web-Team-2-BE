package com.yapp.web2.web.dto.comment.response

import com.yapp.web2.domain.comment.model.Comment

data class CommentPostResponse(
    val commentId: Long,
    val contents: String
) {
    companion object {
        fun of(comment: Comment): CommentPostResponse {
            return CommentPostResponse(
                comment.id,
                comment.contents,
            )
        }
    }
}
