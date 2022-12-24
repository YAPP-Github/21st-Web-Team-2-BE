package com.yapp.web2.web.dto.index.response

import com.yapp.web2.web.dto.vote.response.VotePreviewResponse

data class MainPageResponse(
    val popularVotes: List<VotePreviewResponse>,
    val newestVotes: List<VotePreviewResponse>?,
    val lastVotesIndex: Long?,
    val hasNext: Boolean?
) {
}
