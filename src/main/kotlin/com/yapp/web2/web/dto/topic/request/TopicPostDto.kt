package com.yapp.web2.web.dto.topic.request

import com.yapp.web2.domain.topic.model.TopicCategory
import com.yapp.web2.web.dto.voteoption.request.VoteOptionPostDto

data class TopicPostDto(
    val title: String,
    val contents: String,
    val voteOptions: List<VoteOptionPostDto>,
    val topicCategory: TopicCategory,
    val hashTags: List<String>,
)
