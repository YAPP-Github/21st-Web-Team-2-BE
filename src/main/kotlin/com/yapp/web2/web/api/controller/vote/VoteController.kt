package com.yapp.web2.web.api.controller.vote

import com.yapp.web2.domain.vote.application.VoteService
import com.yapp.web2.web.api.response.ApiResponse
import com.yapp.web2.web.dto.vote.response.VotePreviewResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/vote")
@RestController
class VoteController(
    private val voteService: VoteService,
) {

    @GetMapping("/popular")
    fun getPopularVotes(): ApiResponse<List<VotePreviewResponse>> {
        val votesByPopular = voteService.getPopularVotes()

        return ApiResponse.success(votesByPopular)
    }

    @GetMapping("/latest")
    fun getVotesSlice(@RequestParam lastOffset: String?): ApiResponse<List<VotePreviewResponse>> {
        val latestVotesSlice = voteService.getLatestVotesSlice(lastOffset?.toLong()) //TODO toLong() 예외처리

        return ApiResponse.success(latestVotesSlice)
    }
}
