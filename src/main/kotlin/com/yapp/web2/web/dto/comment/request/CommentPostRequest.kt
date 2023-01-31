package com.yapp.web2.web.dto.comment.request

import jakarta.validation.constraints.NotNull

data class CommentPostRequest(
    @field:NotNull
    val topicId: Long,
    @field:NotNull
    val contents: String
)
