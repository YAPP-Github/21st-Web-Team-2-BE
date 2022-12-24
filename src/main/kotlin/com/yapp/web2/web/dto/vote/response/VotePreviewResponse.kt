package com.yapp.web2.web.dto.vote.response

import com.yapp.web2.domain.vote.model.Vote
import com.yapp.web2.web.dto.voteoption.response.VoteOptionPreviewResponse

data class VotePreviewResponse constructor(
    val voteId: Long,
    val title: String,
    val contents: String,
    val createdMemberId: Long,
    val createdMemberName: String,
    val createdMemberProfileImage: String?,
    val commentCount: Int,
    val voteAmount: Int,
    val voteOptionPreviewResponse: List<VoteOptionPreviewResponse>,
) {

    companion object {
        fun of(vote: Vote, voteOptionPreviewResponse: List<VoteOptionPreviewResponse>): VotePreviewResponse {
            return VotePreviewResponse(
                vote.id,
                vote.title,
                vote.contents,
                vote.createdBy.id,
                vote.createdBy.nickname,
                vote.createdBy.profileImageFilename,
                vote.comments.size,
                vote.voteOptions.sumOf { it.voteOptionMembers.size },
                voteOptionPreviewResponse,
            )
        }
    }
}
