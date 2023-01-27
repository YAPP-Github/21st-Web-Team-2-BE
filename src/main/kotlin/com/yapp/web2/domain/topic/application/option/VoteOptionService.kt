package com.yapp.web2.domain.topic.application.option

import com.yapp.web2.common.util.findByIdOrThrow
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.topic.model.option.VoteOptionMember
import com.yapp.web2.domain.topic.repository.TopicRepository
import com.yapp.web2.domain.topic.repository.option.VoteOptionMemberQuerydslRepository
import com.yapp.web2.domain.topic.repository.option.VoteOptionMemberRepository
import com.yapp.web2.domain.topic.repository.option.VoteOptionRepository
import com.yapp.web2.web.dto.voteoption.request.VotePostRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class VoteOptionService(
    private val topicRepository: TopicRepository,
    private val voteOptionRepository: VoteOptionRepository,
    private val voteOptionMemberRepository: VoteOptionMemberRepository,
    private val voteOptionMemberQuerydslRepository: VoteOptionMemberQuerydslRepository,
) {

    /**
     * 1. 동일한 투표 이력이 존재하는 경우 -> 삭제
     * 2. 동일한 투표 게시글에 다른 선택지에 투표한 이력이 있는 경우 -> 변경
     * 3. 투표 게시글에 대한 투표 이력이 없는 경우 -> 추가
     */
    @Transactional
    fun vote(votedBy: Member, requestDto: VotePostRequest) {
        val topic = topicRepository.findByIdOrThrow(requestDto.topicId)
        val votedRecord = voteOptionMemberQuerydslRepository.findByMemberAndTopic(votedBy, topic)

        votedRecord?.let {
            voteOptionMemberRepository.delete(it)
            if (it.voteOption.id != requestDto.voteOptionId) {
                saveVoteRecord(requestDto, votedBy) // 투표 이력은 있지만, 투표 선택지가 다른 경우
            }
        } ?: saveVoteRecord(requestDto, votedBy)
    }

    private fun saveVoteRecord(
        requestDto: VotePostRequest,
        votedBy: Member,
    ) {
        val voteOption = voteOptionRepository.findByIdOrThrow(requestDto.voteOptionId)
        voteOptionMemberRepository.save(
            VoteOptionMember(votedBy, voteOption)
        )
    }
}
