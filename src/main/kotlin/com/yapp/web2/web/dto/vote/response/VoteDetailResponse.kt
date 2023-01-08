package com.yapp.web2.web.dto.vote.response

import com.yapp.web2.domain.vote.model.Vote
import com.yapp.web2.web.dto.member.response.MemberResponse
import com.yapp.web2.web.dto.voteoption.response.VoteOptionPreviewResponse

data class VoteDetailResponse(
    val topicId: Long,
    val title: String,
    val contents: String,
    val member: MemberResponse,
    val commentAmount: Int,
    val voteAmount: Int,
    val liked: Boolean,
    val likedAmount: Int,
    val tags: List<String>,
    val voteOptions: List<VoteOptionPreviewResponse>
) {
    companion object {
        fun of(vote: Vote, commentCount: Int, voteAmount: Int, liked: Boolean, likedAmount: Int, voteOptionPreviewResponse: List<VoteOptionPreviewResponse>): VoteDetailResponse {
            return VoteDetailResponse(
                vote.id,
                vote.title,
                vote.contents,
                MemberResponse.of(vote.createdBy),
                commentCount,
                voteAmount,
                liked,
                likedAmount,
                vote.hashTags.map { it.toString() },
                voteOptionPreviewResponse,
            )
        }
    }
}
