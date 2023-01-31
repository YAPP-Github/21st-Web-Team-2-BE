package com.yapp.web2.domain.topic.application

import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.topic.model.Topic
import com.yapp.web2.domain.topic.model.TopicCategory
import com.yapp.web2.domain.topic.model.VoteType
import com.yapp.web2.domain.topic.model.option.VoteOption
import com.yapp.web2.domain.topic.repository.TopicQuerydslRepository
import com.yapp.web2.domain.topic.repository.TopicRepository
import com.yapp.web2.domain.topic.repository.option.VoteOptionMemberRepository
import com.yapp.web2.web.api.error.BusinessException
import com.yapp.web2.web.api.error.ErrorCode
import com.yapp.web2.web.dto.topic.request.TopicPostRequest
import com.yapp.web2.web.dto.topic.response.TopicDetailResponse
import com.yapp.web2.web.dto.topic.response.TopicPostResponse
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
    private val topicRepository: TopicRepository,
    private val voteOptionMemberRepository: VoteOptionMemberRepository,
) {
    fun getPopularTopics(member: Member? = null): List<TopicPreviewResponse> {
        val popularTopics = topicQuerydslRepository.findPopularTopics()
        return popularTopics.map { topicPreviewVo ->
            TopicPreviewResponse.of(
                topicPreviewVo.topic,
                topicPreviewVo.commentCount.toInt(),
                topicPreviewVo.voteAmount.toInt(),
                getVoteOptionPreviewResponses(topicPreviewVo.topic, member),
            )
        }
    }

    fun getLatestTopicsSlice(
        lastTopicId: Long?,
        topicCategory: TopicCategory?,
        member: Member? = null
    ): Slice<TopicPreviewResponse> {
        val latestTopicSliceVo = topicQuerydslRepository.findLatestTopicsByCategory(lastTopicId, topicCategory)

        return SliceImpl(
            latestTopicSliceVo.topics.map { topicPreviewVo ->
                TopicPreviewResponse.of(
                    topicPreviewVo.topic,
                    topicPreviewVo.commentCount.toInt(),
                    topicPreviewVo.voteAmount.toInt(),
                    getVoteOptionPreviewResponses(topicPreviewVo.topic, member),
                )
            },
            Pageable.unpaged(),
            latestTopicSliceVo.hasNext,
        )
    }

    private fun getVoteOptionPreviewResponses(topic: Topic, member: Member?): List<VoteOptionPreviewResponse> {
        return topic.voteOptions.map { voteOption ->
            VoteOptionPreviewResponse.of(
                voteOption,
                isVoted(voteOption, member)
            )
        }
    }

    private fun isVoted(voteOption: VoteOption, member: Member?): Boolean {
        if (member == null) {
            return false
        }

        return voteOptionMemberRepository.existsByVoteOptionAndVotedBy(voteOption, member)
    }

    fun getTopicDetail(topicId: Long, member: Member? = null): TopicDetailResponse {
        try {
            return topicQuerydslRepository.findTopicDetailById(topicId)?.let { voteVo ->
                TopicDetailResponse.of(
                    voteVo.topic,
                    voteVo.voteAmount.toInt(),
                    voteVo.commentCount.toInt(),
                    false, //TODO 좋아요 여부
                    voteVo.likedAmount.toInt(),
                    getVoteOptionPreviewResponses(voteVo.topic, member),
                )
            } ?: throw BusinessException(ErrorCode.NOT_FOUND_DATA)
        } catch (exception: NoSuchElementException) {
            throw BusinessException(ErrorCode.NOT_FOUND_DATA)
        }
    }

    @Transactional
    fun saveTopic(member: Member, requestDto: TopicPostRequest): TopicPostResponse {
        val voteType = VoteType.from(requestDto.voteOptions[0])

        val topic = Topic(
            requestDto.title ?: nullValueException(),
            requestDto.topicCategory ?: nullValueException(),
            requestDto.contents ?: nullValueException(),
            voteType,
            createdBy = member,
        )

        for (voteOptionDto in requestDto.voteOptions) {
            val voteOption = VoteOption(
                voteOptionDto.text ?: nullValueException(),
                voteOptionDto.image,
                voteOptionDto.codeBlock,
                topic
            )
            topic.addVoteOption(voteOption)
        }
        val savedTopic = topicRepository.save(topic)
        return TopicPostResponse.from(savedTopic)
    }

    private fun nullValueException(): Nothing {
        throw BusinessException(ErrorCode.NULL_VALUE)
    }
}
