package com.yapp.web2.web.dto.voteoption.response

import com.yapp.web2.domain.topic.model.option.VoteOption

data class VoteOptionDetailResponse(
    val voteOptionId: Long,
    val text: String?,
    val image: String?,
    val language: String?,
    val codeBlock: String?,
    val voted: Boolean,
    val voteAmount: Int,
    val votedAmountStatistics: VotedAmountStatisticsResponse,
) {

    companion object {
        fun of(voteOption: VoteOption, voted: Boolean = false, votedAmountStatistics: VotedAmountStatisticsResponse): VoteOptionDetailResponse {
            return VoteOptionDetailResponse(
                voteOption.id,
                voteOption.text,
                voteOption.image,
                voteOption.language,
                voteOption.codeBlock,
                voted,
                voteOption.voteOptionMembers.size,
                votedAmountStatistics,
            )
        }
    }
}
