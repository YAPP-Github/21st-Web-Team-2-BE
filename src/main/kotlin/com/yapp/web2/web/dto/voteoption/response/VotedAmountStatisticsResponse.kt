package com.yapp.web2.web.dto.voteoption.response

data class VotedAmountStatisticsResponse(
    val developerVoteAmount: Int,
    val designerVoteAmount: Int,
    val pmVoteAmount: Int,
    val etcVoteAmount: Int,
) {
}
