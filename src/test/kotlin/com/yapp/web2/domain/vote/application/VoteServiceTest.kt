package com.yapp.web2.domain.vote.application

import com.yapp.web2.domain.member.model.JobCategory
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.vote.model.Vote
import com.yapp.web2.domain.vote.model.VoteType
import com.yapp.web2.domain.vote.model.option.VoteOption
import com.yapp.web2.domain.vote.model.option.VoteOptionMember
import com.yapp.web2.domain.vote.repository.VoteRepository
import com.yapp.web2.web.api.error.BusinessException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

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
        val latestVotesSlice = voteService.getLatestVotesSlice(null).content

        //then
        assertThat(latestVotesSlice).hasSize(2)

        val voteOptionPreview = latestVotesSlice[0]
        assertThat(voteOptionPreview.voteAmount).isEqualTo(3)

        assertThat(voteOptionPreview.voteOptions[0].votedAmount).isEqualTo(2)
        assertThat(voteOptionPreview.voteOptions[1].votedAmount).isEqualTo(1)
    }

    @Test
    fun `인기순 투표 게시글 조회 테스트`() {
        //given
        saveDummyVotesDetailWithVoteAmount(10)

        //when
        val popularVotes = voteService.getPopularVotes()

        //then
        assertThat(popularVotes).hasSize(4)

        val voteOptionPreview = popularVotes[0]
        assertThat(voteOptionPreview.voteAmount).isEqualTo(20)
    }

    @Test
    fun `투표 게시글 상세 조회 테스트`() {
        //given
        val dummyVotes = saveDummyVotesDetailWithVoteAmount(10)

        //when
        val findVote = dummyVotes[0]
        val voteDetail = voteService.getVoteDetail(findVote.id)

        //then
        assertThat(voteDetail.title).isEqualTo(findVote.title)
    }

    @Test
    fun `존재하지 않는 투표 게시글 상세 조회시 예외 발생`() {
        //given
        val invalidVoteId = 400000L

        assertThatThrownBy { voteService.getVoteDetail(invalidVoteId) }
            .isInstanceOf(BusinessException::class.java)
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
            voteOptionB.addVoteOptionMember(VoteOptionMember(memberC, voteOptionB))
        }


        return voteRepository.saveAll(sampleVotes)
    }

    private fun saveDummyVotesDetailWithVoteAmount(amount: Int): MutableList<Vote> {
        val memberA = Member("MemberA", JobCategory.DEVELOPER, 3)
        memberRepository.saveAll(listOf(memberA))

        val sampleVotes: MutableList<Vote> = mutableListOf()
        for (i in 1..amount) {
            sampleVotes.add(Vote("Vote$i", JobCategory.DEVELOPER, "Content$i", VoteType.TEXT, createdBy = memberA))
        }

        for (vote in sampleVotes) {
            vote.addVoteOption(VoteOption("${vote.contents} OptionA", null, null, vote))
            vote.addVoteOption(VoteOption("${vote.contents} OptionB", null, null, vote))
        }

        // 투표 게시글 크기의 2배 만큼 투표수를 받음
        // ex) voteSize = 10, 게시글의 투표수는 20, 18, 16, ... 씩 줄어듦
        for (i in 0 until sampleVotes.size) {
            val voteOptionA = sampleVotes[i].voteOptions[0]
            val voteOptionB = sampleVotes[i].voteOptions[1]

            for (j in 0..i) {
                voteOptionA.addVoteOptionMember(VoteOptionMember(memberA, voteOptionA))
                voteOptionB.addVoteOptionMember(VoteOptionMember(memberA, voteOptionB))
            }
        }
        return voteRepository.saveAll(sampleVotes)
    }
}


