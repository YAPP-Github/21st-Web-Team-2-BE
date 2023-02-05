package com.yapp.web2.web.dto.topic.response

import com.yapp.web2.domain.topic.model.Topic
import com.yapp.web2.domain.topic.model.TopicCategory
import com.yapp.web2.web.dto.member.response.MemberResponse
import com.yapp.web2.web.dto.voteoption.response.VoteOptionPreviewResponse

data class TopicDetailResponse(
    val topicId: Long,
    val title: String,
    val contents: String,
    val topicCategory: TopicCategory,
    val member: MemberResponse,
    val commentAmount: Int,
    val voteAmount: Int,
    val liked: Boolean,
    val likeAmount: Int,
    val tags: List<String>,
    val voteOptions: List<VoteOptionPreviewResponse>
) {
    companion object {
        fun of(topic: Topic, commentCount: Int, voteAmount: Int, liked: Boolean, likedAmount: Int, voteOptionPreviewResponse: List<VoteOptionPreviewResponse>): TopicDetailResponse {
            return TopicDetailResponse(
                topic.id,
                topic.title,
                topic.contents,
                topic.topicCategory,
                MemberResponse.of(topic.createdBy),
                commentCount,
                voteAmount,
                liked,
                likedAmount,
                topic.hashTags.map { it.hashTag },
                voteOptionPreviewResponse,
            )
        }
    }
}
