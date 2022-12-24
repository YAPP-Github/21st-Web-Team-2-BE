package com.yapp.web2.domain.vote.application

import com.yapp.web2.domain.member.model.JobCategory
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.vote.model.Vote
import com.yapp.web2.domain.vote.model.VoteType
import com.yapp.web2.domain.vote.model.option.VoteOption
import com.yapp.web2.domain.vote.model.option.VoteOptionMember
import com.yapp.web2.domain.vote.repository.VoteRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest

@SpringBootTest
internal class VoteServiceTest @Autowired constructor(
    val voteService: VoteService,
    val voteRepository: VoteRepository,
    val memberRepository: MemberRepository,
) {

    @BeforeEach
    fun beforeEach() {
        memberRepository.deleteAll()
        voteRepository.deleteAll()
    }


    @Test
    fun `최신순 페이지 조회 테스트`() {
        //given
        saveDummyVotesDetail(2)

        //when
        val votesByPageRequest = voteService.getVotesByPageRequest(null, PageRequest.of(0, 5))

        //then
        assertThat(votesByPageRequest).hasSize(2)

        val voteOptionPreview = votesByPageRequest[0]
        assertThat(voteOptionPreview.voteAmount).isEqualTo(3)

        assertThat(voteOptionPreview.voteOptionPreviewResponse[0].votedAmount).isEqualTo(2)
        assertThat(voteOptionPreview.voteOptionPreviewResponse[1].votedAmount).isEqualTo(1)
    }

    private fun saveDummyVotesDetail(amount: Int): MutableList<Vote> {
        // 유저 생성
        val memberA = Member("MemberA", JobCategory.DEVELOPER, 3)
        val memberB = Member("MemberB", JobCategory.DESIGNER, 5)
        val memberC = Member("MemberC", JobCategory.PRODUCT_MANAGER, 1)
        memberRepository.saveAll(listOf(memberA, memberB, memberC))

        // 투표 게시글 생성
        val sampleVotes: MutableList<Vote> = mutableListOf()
        for (i in 1..amount) {
            sampleVotes.add(Vote("Vote$i", JobCategory.DEVELOPER, "Content$i", VoteType.TEXT, createdBy = memberA))
        }

        // 투표 게시글 마다 2개의 옵션이 존재
        // 첫번째 옵션에 2개, 두번째 옵션에 1개가 투표됨
        for (vote in sampleVotes) {
            val voteOptionA = VoteOption("${vote.contents} OptionA", null, null, vote)
            val voteOptionB = VoteOption("${vote.contents} OptionB", null, null, vote)

            vote.addVoteOption(voteOptionA)
            vote.addVoteOption(voteOptionB)

            voteOptionA.addVoteOptionMember(VoteOptionMember(memberA, voteOptionA))
            voteOptionA.addVoteOptionMember(VoteOptionMember(memberB, voteOptionA))
            voteOptionA.addVoteOptionMember(VoteOptionMember(memberC, voteOptionB))
        }


        return voteRepository.saveAll(sampleVotes)
    }

}


