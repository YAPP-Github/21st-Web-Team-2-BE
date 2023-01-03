package com.yapp.web2.domain.vote.application.vo

data class LatestVoteSliceVo(
    val votes: MutableList<VotePreviewVo>,
    val hasNext: Boolean,
) {
}
