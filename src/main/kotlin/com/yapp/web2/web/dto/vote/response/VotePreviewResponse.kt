package com.yapp.web2.web.dto.vote.response

import com.yapp.web2.web.dto.voteoption.response.VoteOptionPreviewResponse

data class VotePreviewResponse constructor(
    val title: String,
    val content: String,
    val createdMemberName: String,
    val commentCount: Int,
    val voteAmount: Int,
    val voteOptionPreviewResponse: List<VoteOptionPreviewResponse>,
) {
}
