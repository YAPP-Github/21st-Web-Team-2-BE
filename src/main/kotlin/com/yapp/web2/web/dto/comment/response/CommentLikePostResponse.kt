package com.yapp.web2.web.dto.comment.response

data class CommentLikePostResponse(
    val commentId: Long,
    val liked: Boolean,
)
