package com.yapp.web2.web.dto.topic.request

import com.yapp.web2.domain.topic.model.TopicCategory
import com.yapp.web2.web.dto.voteoption.request.VoteOptionPostRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class TopicPostRequest(
    @field:NotBlank
    val title: String?,

    @field:NotBlank
    val contents: String?,

    @field:NotNull
    val voteOptions: List<VoteOptionPostRequest>,

    @field:NotNull
    val topicCategory: TopicCategory?,
    val tags: List<String>?,
)
