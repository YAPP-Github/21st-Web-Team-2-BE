package com.yapp.web2.domain.topic.application

import com.yapp.web2.domain.topic.model.Topic
import com.yapp.web2.domain.topic.repository.TopicQuerydslRepository
import com.yapp.web2.web.api.error.BusinessException
import com.yapp.web2.web.api.error.ErrorCode
import com.yapp.web2.web.dto.topic.response.TopicDetailResponse
import com.yapp.web2.web.dto.topic.response.TopicPreviewResponse
import com.yapp.web2.web.dto.voteoption.response.VoteOptionPreviewResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Transactional(readOnly = true)
@Service
class TopicService(
    private val topicQuerydslRepository: TopicQuerydslRepository,
) {
    fun getPopularTopics(): List<TopicPreviewResponse> {
        val popularTopics = topicQuerydslRepository.findPopularTopics()
        return popularTopics.map { topicPreviewVo ->
            TopicPreviewResponse.of(
                topicPreviewVo.topic,
                topicPreviewVo.commentCount.toInt(),
                topicPreviewVo.voteAmount.toInt(),
                getVoteOptionPreviewResponses(topicPreviewVo.topic),
            )
        }
    }

    fun getLatestTopicsSlice(lastTopicId: Long?): Slice<TopicPreviewResponse> {
        val latestTopicSliceVo = topicQuerydslRepository.findLatestTopicsByCategory(lastTopicId)

        return SliceImpl(
            latestTopicSliceVo.topics.map { topicPreviewVo ->
                TopicPreviewResponse.of(
                    topicPreviewVo.topic,
                    topicPreviewVo.commentCount.toInt(),
                    topicPreviewVo.voteAmount.toInt(),
                    getVoteOptionPreviewResponses(topicPreviewVo.topic),
                )
            },
            Pageable.unpaged(),
            latestTopicSliceVo.hasNext,
        )
    }

    private fun getVoteOptionPreviewResponses(topic: Topic): List<VoteOptionPreviewResponse> {
        return topic.voteOptions.map { voteOption ->
            VoteOptionPreviewResponse.of(
                voteOption,
                //TODO 로그인 한 회원에 대한 투표 여부 응답
            )
        }
    }

    fun getTopicDetail(topicId: Long): TopicDetailResponse {
        try {
            return topicQuerydslRepository.findTopicDetailById(topicId)?.let { voteVo ->
                TopicDetailResponse.of(
                    voteVo.topic,
                    voteVo.voteAmount.toInt(),
                    voteVo.commentCount.toInt(),
                    false, //TODO 좋아요 여부
                    voteVo.likedAmount.toInt(),
                    getVoteOptionPreviewResponses(voteVo.topic),
                )
            } ?: throw BusinessException(ErrorCode.NOT_FOUND_DATA)
        } catch (exception: NoSuchElementException) {
            throw BusinessException(ErrorCode.NOT_FOUND_DATA)
        }
    }
}
