package com.yapp.web2.web.dto.vote.response

data class VotePreviewSliceResponse(
    val votePreviews: List<VotePreviewResponse>?,
    val hasNext: Boolean,
    val lastVotesIndex: Long?,
)
