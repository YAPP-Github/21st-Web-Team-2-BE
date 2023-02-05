package com.yapp.web2.web.api.controller.comment

import com.yapp.web2.common.annotation.CurrentMember
import com.yapp.web2.common.annotation.NonMember
import com.yapp.web2.domain.comment.application.CommentService
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.web.api.error.BusinessException
import com.yapp.web2.web.api.error.ErrorCode
import com.yapp.web2.web.api.response.ApiResponse
import com.yapp.web2.web.dto.comment.request.CommentLikePostRequest
import com.yapp.web2.web.dto.comment.request.CommentPostRequest
import com.yapp.web2.web.dto.comment.response.CommentDetailResponse
import com.yapp.web2.web.dto.comment.response.CommentLikePostResponse
import com.yapp.web2.web.dto.comment.response.CommentPostResponse
import jakarta.validation.Valid
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/comment")
@RestController
class CommentController(
    private val commentService: CommentService,
) {

    @NonMember
    @GetMapping("/{voteId}/latest")
    fun getCommentsSlice(
        @CurrentMember member: Member?,
        @PathVariable voteId: String,
        @RequestParam lastOffset: String?
    ): ApiResponse<List<CommentDetailResponse>> {
        val latestCommentSlice =
            commentService.getLatestComments(voteId.toLong(), lastOffset?.toLong(), member) //TODO toLong() 예외처리

        return ApiResponse.success(latestCommentSlice)
    }

    @PostMapping
    fun createComment(
        @CurrentMember member: Member,
        @Valid @RequestBody requestDto: CommentPostRequest,
        bindingResult: BindingResult,
    ): ApiResponse<CommentPostResponse> {
        if (bindingResult.hasErrors()) {
            throw BusinessException(ErrorCode.NULL_VALUE)
        }
        val commentPostResponse = commentService.saveComment(member, requestDto)

        return ApiResponse.success(commentPostResponse)
    }

    @PostMapping("/likes")
    fun likeComment(
        @CurrentMember member: Member,
        @Valid @RequestBody requestDto: CommentLikePostRequest,
        bindingResult: BindingResult,
    ): ApiResponse<CommentLikePostResponse> {
        if (bindingResult.hasErrors()) {
            throw BusinessException(ErrorCode.NULL_VALUE)
        }
        val commentLikePostResponse = commentService.toggleCommentLikes(member, requestDto)

        return ApiResponse.success(commentLikePostResponse)
    }

}
