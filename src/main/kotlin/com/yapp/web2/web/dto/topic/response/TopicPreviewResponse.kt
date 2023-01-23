package com.yapp.web2.web.dto.topic.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.yapp.web2.domain.topic.model.Topic
import com.yapp.web2.domain.topic.model.TopicCategory
import com.yapp.web2.web.api.response.OffsetIdSupport
import com.yapp.web2.web.dto.member.response.MemberResponse
import com.yapp.web2.web.dto.voteoption.response.VoteOptionPreviewResponse

data class TopicPreviewResponse(
    @JsonProperty("topicId")
    override val offsetId: Long,
    val title: String,
    val contents: String,
    val topicCategory: TopicCategory,
    val member: MemberResponse,
    val commentAmount: Int,
    val voteAmount: Int,
    val voteOptions: List<VoteOptionPreviewResponse>,
) : OffsetIdSupport {

    companion object {
        fun of(topic: Topic, commentCount: Int, voteAmount: Int, voteOptionPreviewResponse: List<VoteOptionPreviewResponse>): TopicPreviewResponse {
            return TopicPreviewResponse(
                topic.id,
                topic.title,
                topic.contents,
                topic.topicCategory,
                MemberResponse.of(topic.createdBy),
                commentCount,
                voteAmount,
                voteOptionPreviewResponse,
            )
        }
    }
}
