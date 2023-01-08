package com.yapp.web2.web.dto.comment.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.yapp.web2.domain.comment.model.Comment
import com.yapp.web2.domain.member.model.JobCategory
import com.yapp.web2.web.api.response.OffsetIdSupport

data class CommentDetailResponse(
    @JsonProperty("commentId")
    override val offsetId: Long,
    val memberId: Long,
    val memberName: String,
    val memberProfileImage: String?,
    val memberJobCategory: String,
    val memberWorkingYears: Int,
    val commentContent: String,
    val likeAmount: Int,
    val liked: Boolean,
) : OffsetIdSupport {

    companion object{
        fun of(comment:Comment, likeAmount: Int, liked: Boolean): CommentDetailResponse {
            return CommentDetailResponse(
                comment.id,
                comment.createdBy.id,
                comment.createdBy.nickname,
                comment.createdBy.profileImageFilename,
                comment.createdBy.jobCategory,
                comment.createdBy.workingYears,
                comment.contents,
                likeAmount,
                liked,
            )
        }
    }
}
