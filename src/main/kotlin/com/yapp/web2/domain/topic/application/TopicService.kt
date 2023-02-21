package com.yapp.web2.domain.topic.application

import com.yapp.web2.common.util.findByIdOrThrow
import com.yapp.web2.domain.like.model.TopicLikes
import com.yapp.web2.domain.like.repository.TopicLikesRepository
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.topic.model.HashTag
import com.yapp.web2.domain.topic.model.Topic
import com.yapp.web2.domain.topic.model.TopicCategory
import com.yapp.web2.domain.topic.model.VoteType
import com.yapp.web2.domain.topic.model.option.VoteOption
import com.yapp.web2.domain.topic.repository.TopicQuerydslRepository
import com.yapp.web2.domain.topic.repository.TopicRepository
import com.yapp.web2.domain.topic.repository.option.VoteOptionMemberRepository
import com.yapp.web2.web.api.error.BusinessException
import com.yapp.web2.web.api.error.ErrorCode
import com.yapp.web2.web.dto.topic.request.TopicLikePostRequest
import com.yapp.web2.web.dto.topic.request.TopicPostRequest
import com.yapp.web2.web.dto.topic.request.TopicSearchRequest
import com.yapp.web2.web.dto.topic.response.TopicDetailResponse
import com.yapp.web2.web.dto.topic.response.TopicLikePostResponse
import com.yapp.web2.web.dto.topic.response.TopicPostResponse
import com.yapp.web2.web.dto.topic.response.TopicPreviewResponse
import com.yapp.web2.web.dto.voteoption.response.VoteOptionDetailResponse
import com.yapp.web2.web.dto.voteoption.response.VoteOptionPreviewResponse
import com.yapp.web2.web.dto.voteoption.response.VotedAmountStatisticsResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


const val DEVELOPER_FILTER = "개발"
const val DESIGNER_FILTER = "디자인"
const val PM_FILTER = "기획"

@Transactional(readOnly = true)
@Service
class TopicService(
    private val topicQuerydslRepository: TopicQuerydslRepository,
    private val topicRepository: TopicRepository,
    private val voteOptionMemberRepository: VoteOptionMemberRepository,
    private val topicLikesRepository: TopicLikesRepository,
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
                    isLiked(voteVo.topic, member),
                    voteVo.likedAmount.toInt(),
                    getVoteOptionDetailResponses(voteVo.topic, member),
                )
            } ?: throw BusinessException(ErrorCode.NOT_FOUND_DATA)
        } catch (exception: NoSuchElementException) {
            throw BusinessException(ErrorCode.NOT_FOUND_DATA)
        }
    }

    private fun getVoteOptionDetailResponses(topic: Topic, member: Member?): List<VoteOptionDetailResponse> {
        return topic.voteOptions.map { voteOption ->
            VoteOptionDetailResponse.of(
                voteOption,
                isVoted(voteOption, member),
                calculateVotedStatistics(voteOption),
            )
        }
    }

    private fun calculateVotedStatistics(voteOption: VoteOption): VotedAmountStatisticsResponse {
        val voteOptionMembers = voteOptionMemberRepository.findVotedMembersByVoteOptionId(voteOption)

        val developerVotedAmount = voteOptionMembers.stream()
            .map { it.votedBy }
            .filter { it.jobCategory == DEVELOPER_FILTER }
            .toList()
            .size

        val designerVotedAmount = voteOptionMembers.stream()
            .map { it.votedBy }
            .filter { it.jobCategory == DESIGNER_FILTER }
            .toList()
            .size

        val plannerVotedAmount = voteOptionMembers.stream()
            .map { it.votedBy }
            .filter { it.jobCategory == PM_FILTER }
            .toList().size

        val etcVotedAmount = voteOptionMembers.size - (developerVotedAmount + designerVotedAmount + plannerVotedAmount)
        return VotedAmountStatisticsResponse(
            developerVotedAmount,
            designerVotedAmount,
            plannerVotedAmount,
            etcVotedAmount,
        )
    }

    private fun isLiked(topic: Topic, member: Member?): Boolean {
        if (member == null) {
            return false
        }

        return topicLikesRepository.existsByTopicAndLikedBy(topic, member)
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

        requestDto.tags?.map { tag -> topic.addTags(HashTag(topic, tag)) }

        for (voteOptionDto in requestDto.voteOptions) {
            val voteOption = VoteOption(
                voteOptionDto.text ?: nullValueException(),
                voteOptionDto.image,
                voteOptionDto.language,
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

    @Transactional
    fun toggleTopicLikes(member: Member, requestDto: TopicLikePostRequest): TopicLikePostResponse {
        val topic = topicRepository.findByIdOrThrow(requestDto.topicId)
        val topicLikes = topicLikesRepository.findByLikedByAndTopic(member, topic)

        return if (topicLikes == null) {
            return likeTopic(member, topic)
        } else {
            unlikeTopic(topicLikes)
        }
    }

    fun searchTopic(
        searchRequest: TopicSearchRequest,
        pageable: Pageable,
        member: Member? = null,
    ): Slice<TopicPreviewResponse> {
        val topic = topicQuerydslRepository.searchByTitleAndContentOrTag(searchRequest, pageable)
        return topic.map { topicPreviewVo ->
            TopicPreviewResponse.of(
                topicPreviewVo.topic,
                topicPreviewVo.commentCount.toInt(),
                topicPreviewVo.voteAmount.toInt(),
                getVoteOptionPreviewResponses(topicPreviewVo.topic, member),
            )
        }
    }

    private fun unlikeTopic(topicLikes: TopicLikes): TopicLikePostResponse {
        topicLikesRepository.delete(topicLikes)
        return TopicLikePostResponse(topicLikes.topic.id, false)
    }

    private fun likeTopic(member: Member, topic: Topic): TopicLikePostResponse {
        val topicLikes = TopicLikes(member, topic)
        topic.addTopicLike(topicLikes)

        topicLikesRepository.save(topicLikes)
        return TopicLikePostResponse(topicLikes.topic.id, true)
    }
}
