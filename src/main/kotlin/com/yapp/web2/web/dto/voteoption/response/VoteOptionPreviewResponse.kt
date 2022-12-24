package com.yapp.web2.web.dto.voteoption.response

data class VoteOptionPreviewResponse(
    val text: String?,
    val voteOptionImageFilename: String?,
    val codeBlock: String?,
    val voted: Boolean,
    val votedAmount: Int,
) {
}
