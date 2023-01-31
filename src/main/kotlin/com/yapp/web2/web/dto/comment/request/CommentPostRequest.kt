package com.yapp.web2.web.dto.comment.request

data class CommentPostRequest(
    val topicId: Long,
    val contents: String
)
