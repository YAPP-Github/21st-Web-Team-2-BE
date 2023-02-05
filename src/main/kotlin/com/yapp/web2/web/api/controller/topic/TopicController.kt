package com.yapp.web2.web.api.controller.topic

import com.yapp.web2.common.annotation.CurrentMember
import com.yapp.web2.common.annotation.NonMember
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.topic.application.TopicService
import com.yapp.web2.domain.topic.model.TopicCategory
import com.yapp.web2.web.api.error.BusinessException
import com.yapp.web2.web.api.error.ErrorCode
import com.yapp.web2.web.api.response.ApiResponse
import com.yapp.web2.web.dto.topic.request.TopicLikePostRequest
import com.yapp.web2.web.dto.topic.request.TopicPostRequest
import com.yapp.web2.web.dto.topic.request.TopicSearchRequest
import com.yapp.web2.web.dto.topic.response.TopicDetailResponse
import com.yapp.web2.web.dto.topic.response.TopicLikePostResponse
import com.yapp.web2.web.dto.topic.response.TopicPostResponse
import com.yapp.web2.web.dto.topic.response.TopicPreviewResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RequestMapping("/api/v1/topic")
@RestController
class TopicController(
    private val topicService: TopicService,
) {
    @NonMember
    @GetMapping("/popular")
    fun getPopularTopics(@CurrentMember member: Member?): ApiResponse<List<TopicPreviewResponse>> {
        val topicsByPopular = topicService.getPopularTopics(member)

        return ApiResponse.success(topicsByPopular)
    }

    @NonMember
    @GetMapping("/latest")
    fun getTopicsSlice(
        @CurrentMember member: Member?,
        @RequestParam lastOffset: String?,
        @RequestParam topicCategory: TopicCategory?
    ): ApiResponse<List<TopicPreviewResponse>> {
        val latestTopicsSlice =
            topicService.getLatestTopicsSlice(lastOffset?.toLong(), topicCategory, member) //TODO toLong() 예외처리

        return ApiResponse.success(latestTopicsSlice)
    }
    @NonMember
    @GetMapping("/{topicId}")
    fun getTopicDetail(
        @CurrentMember member: Member?,
        @PathVariable topicId: String): ApiResponse<TopicDetailResponse> {
        val topicDetail = topicService.getTopicDetail(topicId.toLong(), member) //TODO toLong() 예외처리

        return ApiResponse.success(topicDetail)
    }

    @PostMapping
    fun createTopic(
        @CurrentMember member: Member,
        @Valid @RequestBody topicPostRequest: TopicPostRequest,
    ): ApiResponse<TopicPostResponse> {
        val topicPostResponse = topicService.saveTopic(member, topicPostRequest)

        return ApiResponse.success(topicPostResponse)
    }

    @PostMapping("/likes")
    fun likeTopic(
        @CurrentMember member: Member,
        @Valid @RequestBody topicLikePostRequest: TopicLikePostRequest,
    ): ApiResponse<TopicLikePostResponse> {
        val topicLikesResponse = topicService.toggleTopicLikes(member, topicLikePostRequest)
        return ApiResponse.success(topicLikesResponse)
    }

    @NonMember
    @PostMapping("/search")
    fun searchTopic(
        @CurrentMember member: Member?,
        @RequestBody topicSearchRequest: TopicSearchRequest,
        @RequestParam(required = false, defaultValue = "0") @Min(0) page: Int,
    ): ApiResponse<List<TopicPreviewResponse>> {
        val pageable = PageRequest.of(page, 6)
        return ApiResponse.successWithPage(topicService.searchTopic(topicSearchRequest, pageable, member))
    }
}
