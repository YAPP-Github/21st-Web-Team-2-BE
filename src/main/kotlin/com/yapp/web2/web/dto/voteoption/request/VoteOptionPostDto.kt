package com.yapp.web2.web.dto.voteoption.request

data class VoteOptionPostDto(
    val text: String,
    val voteOptionImageFilename: String?,
    val codeBlock: String?,
) {
}
