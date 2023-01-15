package com.yapp.web2.web.dto.voteoption.response

import com.yapp.web2.domain.topic.model.option.VoteOption

data class VoteOptionPreviewResponse(
    val id: Long,
    val text: String?,
    val voteOptionImageFilename: String?,
    val codeBlock: String?,
    val voted: Boolean,
    val votedAmount: Int,
) {

    companion object {
        fun of(voteOption: VoteOption, voted: Boolean = false): VoteOptionPreviewResponse {
            return VoteOptionPreviewResponse(
                voteOption.id,
                voteOption.text,
                voteOption.voteOptionImageFilename,
                voteOption.codeBlock,
                voted,
                voteOption.voteOptionMembers.size
            )
        }
    }
}
