package com.yapp.web2.web.api.controller.voteoption

import com.yapp.web2.common.annotation.CurrentMember
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.topic.application.option.VoteOptionService
import com.yapp.web2.web.api.error.BusinessException
import com.yapp.web2.web.api.error.ErrorCode
import com.yapp.web2.web.api.response.ApiResponse
import com.yapp.web2.web.dto.voteoption.request.VotePostRequest
import jakarta.validation.Valid
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/vote/option")
@RestController
class VoteOptionController(
    private val voteOptionService: VoteOptionService,
) {

    @PostMapping
    fun vote(
        @CurrentMember votedBy: Member,
        @Valid @RequestBody votePostRequest: VotePostRequest,
    ): ApiResponse<Nothing> {
        voteOptionService.vote(votedBy, votePostRequest)

        return ApiResponse.success()
    }
}
