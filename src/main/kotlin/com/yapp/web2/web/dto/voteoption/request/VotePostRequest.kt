package com.yapp.web2.web.dto.voteoption.request

import jakarta.validation.constraints.NotNull

data class VotePostRequest(
    @NotNull
    val topicId: Long,
    @NotNull
    val voteOptionId: Long,
) {

}
