package com.yapp.web2.web.api.controller.comment

import com.yapp.web2.domain.comment.application.CommentService
import com.yapp.web2.web.api.response.ApiResponse
import com.yapp.web2.web.dto.comment.response.CommentDetailResponse
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/comment")
@RestController
class CommentController(
    private val commentService: CommentService,
) {

    @GetMapping("/{voteId}/latest")
    fun getVotesSlice(@PathVariable voteId: String, @RequestParam lastOffset: String?): ApiResponse<List<CommentDetailResponse>> {
        val latestCommentSlice = commentService.getLatestComments(voteId.toLong(), lastOffset?.toLong()) //TODO toLong() 예외처리

        return ApiResponse.success(latestCommentSlice)
    }

}
