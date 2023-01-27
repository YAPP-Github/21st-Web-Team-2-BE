package com.yapp.web2.web.api.controller.voteoption

import com.yapp.web2.domain.jwt.application.JwtService
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
    private val jwtService: JwtService,
)  {

    @PostMapping
    fun vote(
        @RequestHeader("Authorization") accessToken: String,
        @Valid @RequestBody votePostRequest: VotePostRequest,
        bindingResult: BindingResult,
    ): ApiResponse<Nothing> {
        if(bindingResult.hasErrors()) {
            throw BusinessException(ErrorCode.NULL_VALUE)
        }

        val votedBy = jwtService.findAccessTokenMember(accessToken)
        voteOptionService.vote(votedBy, votePostRequest)

        return ApiResponse.success()
    }
}
