package com.yapp.web2.web.dto.topic.response

import com.yapp.web2.domain.topic.model.Topic
import com.yapp.web2.domain.topic.model.VoteType

data class TopicPostResponse(
    val topicId: Long,
    val title: String,
    val voteType: VoteType,
    val postMemberNickname: String,
) {
    companion object {
        fun from(topic: Topic): TopicPostResponse {
            return TopicPostResponse(
                topic.id,
                topic.title,
                topic.voteType,
                topic.createdBy.nickname,
            )
        }
    }
}
