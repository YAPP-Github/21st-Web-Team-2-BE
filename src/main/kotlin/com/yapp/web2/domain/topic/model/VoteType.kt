package com.yapp.web2.domain.topic.model

import com.yapp.web2.web.dto.voteoption.request.VoteOptionPostDto

enum class VoteType {
    TEXT,
    TEXT_IMAGE,
    TEXT_IMAGE_CODE_BLOCK,
    TEXT_CODE_BLOCK, ;

    companion object {
        fun from(voteOptionPostDto: VoteOptionPostDto): VoteType {
            return if (voteOptionPostDto.codeBlock != null
                && voteOptionPostDto.voteOptionImageFilename != null
            ) {
                TEXT_IMAGE_CODE_BLOCK
            } else if (voteOptionPostDto.codeBlock != null) {
                TEXT_CODE_BLOCK
            } else if (voteOptionPostDto.voteOptionImageFilename != null) {
                TEXT_IMAGE
            } else {
                TEXT
            }
        }
    }
}
