package com.yapp.web2.domain.topic.model

import com.yapp.web2.web.dto.voteoption.request.VoteOptionPostRequest

enum class VoteType {
    TEXT,
    TEXT_IMAGE,
    TEXT_IMAGE_CODE_BLOCK,
    TEXT_CODE_BLOCK, ;

    companion object {
        fun from(voteOptionPostRequest: VoteOptionPostRequest): VoteType {
            return if (voteOptionPostRequest.codeBlock != null
                && voteOptionPostRequest.image != null
            ) {
                TEXT_IMAGE_CODE_BLOCK
            } else if (voteOptionPostRequest.codeBlock != null) {
                TEXT_CODE_BLOCK
            } else if (voteOptionPostRequest.image != null) {
                TEXT_IMAGE
            } else {
                TEXT
            }
        }
    }
}
