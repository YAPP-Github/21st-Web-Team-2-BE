package com.yapp.web2.web.api.controller.topic

import com.yapp.web2.domain.topic.application.TopicService
import com.yapp.web2.domain.topic.model.TopicCategory
import com.yapp.web2.web.api.response.ApiResponse
import com.yapp.web2.web.dto.topic.response.TopicDetailResponse
import com.yapp.web2.web.dto.topic.response.TopicPreviewResponse
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/topic")
@RestController
class TopicController(
    private val topicService: TopicService,
) {

    @GetMapping("/popular")
    fun getPopularTopics(): ApiResponse<List<TopicPreviewResponse>> {
        val topicsByPopular = topicService.getPopularTopics()

        return ApiResponse.success(topicsByPopular)
    }

    @GetMapping("/latest")
    fun getTopicsSlice(@RequestParam lastOffset: String?, @RequestParam topicCategory: TopicCategory?): ApiResponse<List<TopicPreviewResponse>> {
        val latestTopicsSlice = topicService.getLatestTopicsSlice(lastOffset?.toLong(), topicCategory) //TODO toLong() 예외처리

        return ApiResponse.success(latestTopicsSlice)
    }

    @GetMapping("/{topicId}")
    fun getTopicDetail(@PathVariable topicId: String): ApiResponse<TopicDetailResponse> {
        val topicDetail = topicService.getTopicDetail(topicId.toLong()) //TODO toLong() 예외처리

        return ApiResponse.success(topicDetail)
    }
}
