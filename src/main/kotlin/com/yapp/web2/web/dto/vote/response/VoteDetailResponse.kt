package com.yapp.web2.web.dto.vote.response

import com.yapp.web2.domain.vote.model.Vote
import com.yapp.web2.web.dto.voteoption.response.VoteOptionPreviewResponse

data class VoteDetailResponse(
    val voteId: Long,
    val title: String,
    val contents: String,
    val createdMemberId: Long,
    val createdMemberName: String,
    val createdMemberProfileImage: String?,
    val commentAmount: Int,
    val voteAmount: Int,
    val liked: Boolean,
    val voteOptionPreviewResponse: List<VoteOptionPreviewResponse>
) {
    companion object {
        fun of(vote: Vote, commentCount: Int, voteAmount: Int, liked: Boolean, voteOptionPreviewResponse: List<VoteOptionPreviewResponse>): VoteDetailResponse {
            return VoteDetailResponse(
                vote.id,
                vote.title,
                vote.contents,
                vote.createdBy.id,
                vote.createdBy.nickname,
                vote.createdBy.profileImageFilename,
                commentCount,
                voteAmount,
                liked,
                voteOptionPreviewResponse,
            )
        }
    }
}
