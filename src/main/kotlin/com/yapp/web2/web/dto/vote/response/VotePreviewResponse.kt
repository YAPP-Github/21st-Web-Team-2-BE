package com.yapp.web2.web.dto.vote.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.yapp.web2.domain.vote.model.Vote
import com.yapp.web2.web.api.response.OffsetIdSupport
import com.yapp.web2.web.dto.voteoption.response.VoteOptionPreviewResponse

data class VotePreviewResponse(
    @JsonProperty("topicId")
    override val offsetId: Long,
    val title: String,
    val contents: String,
    val memberId: Long,
    val memberName: String,
    val memberProfileImage: String?,
    val commentAmount: Int,
    val voteAmount: Int,
    val voteOptions: List<VoteOptionPreviewResponse>,
) : OffsetIdSupport {

    companion object {
        fun of(vote: Vote, commentCount: Int, voteAmount: Int, voteOptionPreviewResponse: List<VoteOptionPreviewResponse>): VotePreviewResponse {
            return VotePreviewResponse(
                vote.id,
                vote.title,
                vote.contents,
                vote.createdBy.id,
                vote.createdBy.nickname,
                vote.createdBy.profileImageFilename,
                commentCount,
                voteAmount,
                voteOptionPreviewResponse,
            )
        }
    }
}
