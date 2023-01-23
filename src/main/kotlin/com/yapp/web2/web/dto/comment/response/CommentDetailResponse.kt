package com.yapp.web2.web.dto.comment.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.yapp.web2.domain.comment.model.Comment
import com.yapp.web2.web.api.response.OffsetIdSupport
import com.yapp.web2.web.dto.member.response.MemberResponse

data class CommentDetailResponse(
    @JsonProperty("commentId")
    override val offsetId: Long,
    val member: MemberResponse,
    val contents: String,
    val likeAmount: Int,
    val liked: Boolean,
) : OffsetIdSupport {

    companion object{
        fun of(comment:Comment, likeAmount: Int, liked: Boolean): CommentDetailResponse {
            return CommentDetailResponse(
                comment.id,
                MemberResponse.of(comment.createdBy),
                comment.contents,
                likeAmount,
                liked,
            )
        }
    }
}
