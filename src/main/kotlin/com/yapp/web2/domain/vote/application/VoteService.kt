package com.yapp.web2.domain.vote.application

import com.yapp.web2.domain.vote.model.Vote
import com.yapp.web2.domain.vote.repository.VoteQuerydslRepository
import com.yapp.web2.domain.vote.repository.VoteRepository
import com.yapp.web2.web.dto.vote.response.VotePreviewResponse
import com.yapp.web2.web.dto.voteoption.response.VoteOptionPreviewResponse
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Transactional(readOnly = true)
@Service
class VoteService(
    private val voteRepository: VoteRepository,
    private val voteQuerydslRepository: VoteQuerydslRepository,
) {

    fun getVotesByPageRequest(lastVoteId: Long? = null): List<VotePreviewResponse> {
        val votes = voteQuerydslRepository.findLatestVotes(lastVoteId)

        return votes.votes.map { vote ->
            VotePreviewResponse.of(
                vote,
                getVoteOptionPreviewResponses(vote),
            )
        }
    }

    private fun getVoteOptionPreviewResponses(vote: Vote): List<VoteOptionPreviewResponse> {
        return vote.voteOptions.map { voteOption ->
            VoteOptionPreviewResponse.of(
                voteOption,
                //TODO 로그인 한 회원에 대한 투표 여부 응답
            )
        }
    }

//    fun getVotesByPopular() {
//        voteRepository.
//    }
}
