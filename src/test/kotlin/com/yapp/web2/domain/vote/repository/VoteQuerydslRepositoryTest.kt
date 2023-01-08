package com.yapp.web2.domain.vote.repository

import com.yapp.web2.common.EntityFactory
import com.yapp.web2.domain.member.model.JobCategory
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.vote.model.Vote
import com.yapp.web2.domain.vote.model.VoteType
import com.yapp.web2.domain.vote.model.option.VoteOption
import com.yapp.web2.domain.vote.model.option.VoteOptionMember
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.PersistenceContext
import jakarta.persistence.PersistenceUnit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
internal class VoteQuerydslRepositoryTest @Autowired constructor(
    val voteRepository: VoteRepository,
    val memberRepository: MemberRepository,
    val voteQuerydslRepository: VoteQuerydslRepository,
) {
    @PersistenceUnit
    lateinit var emf: EntityManagerFactory

    @PersistenceContext
    lateinit var em: EntityManager

    @BeforeEach
    fun beforeEach() {
        memberRepository.deleteAll()
        voteRepository.deleteAll()
    }

    @Test
    fun `투표 페이징 조회 테스트`() {
        //given
        val dummyVoteAmount = 20
        val pageSize = 6

        val dummyVotes = saveDummyVotes(dummyVoteAmount)
        dummyVotes.sortByDescending { it.createdAt }

        //when
        val searchBySlice = voteQuerydslRepository.findLatestVotesByCategory()

        //then
        val content = searchBySlice.votes
        assertThat(content).hasSize(pageSize)
        assertThat(searchBySlice.hasNext).isTrue
    }

    @Test
    fun `투표 페이징 인덱스 조회 테스트`() {
        //given
        val dummyVoteAmount = 20

        val dummyVotes = saveDummyVotes(dummyVoteAmount)
        dummyVotes.sortByDescending { it.createdAt }
        val lastVoteId = dummyVotes[(dummyVoteAmount - 1) - 2].id // 마지막에서 2번째 부터 페이징 조회

        //when
        //우선순위(최신순)으로 정렬된 데이터에서, id가 lastVoteId 이후에서 부터 조회
        val searchBySlice = voteQuerydslRepository.findLatestVotesByCategory(lastVoteId)

        //then
        val content = searchBySlice.votes
        assertThat(content).hasSize(2)
        assertThat(searchBySlice.hasNext).isFalse
    }

    @Test
    fun `투표 카테고리 필터 조회 테스트`() {
        //given
        val dummyVoteAmount = 3
        saveDummyVotes(dummyVoteAmount) // JobCategory == DEVELOPER 인 게시글 3개 저장

        val memberB = EntityFactory.testMemberB()
        memberRepository.save(memberB)
        val sampleVotes: MutableList<Vote> = mutableListOf()
        for (i in 1..3) {
            sampleVotes.add(Vote("Vote$i", JobCategory.DESIGNER, "Content$i", VoteType.TEXT, createdBy = memberB))
        }
        voteRepository.saveAll(sampleVotes) // JobCategory == DESIGNER 인 게시글 3개 저장

        //when
        //우선순위(최신순)으로 정렬된 데이터에서, id가 lastVoteId 이후에서 부터 조회
        val searchBySlice = voteQuerydslRepository.findLatestVotesByCategory(jobCategory = JobCategory.DEVELOPER)

        //then
        val content = searchBySlice.votes
        assertThat(content).hasSize(3)
        assertThat(searchBySlice.hasNext).isFalse
    }

    @Test
    fun `투표 페이징 멤버 페치 조인 테스트`() {
        //given
        saveDummyVotes(10)

        //when
        val searchBySlice = voteQuerydslRepository.findLatestVotesByCategory()

        //then
        val content = searchBySlice.votes
        val loaded = emf.persistenceUnitUtil.isLoaded(content[0].vote.createdBy)
        assertThat(loaded).isTrue
    }

    @Test
    fun `투표 개수 정렬 조회 테스트`() {
        //given
        val voteSize = 6
        saveDummyVotesDetail(voteSize) // voteSize 가 6인 경우, voteAmount = [12, 10, 8, 6]

        val expectedVoteAmounts = mutableListOf<Long>()
        for (i in 0..3) {
            expectedVoteAmounts.add((voteSize - i) * 2.toLong())
        }

        //when
        val findPopularVotes = voteQuerydslRepository.findPopularVotes()

        //then
        assertThat(findPopularVotes).hasSize(4)
        assertThat(findPopularVotes).extracting("voteAmount").isEqualTo(expectedVoteAmounts)
    }

    @Test
    fun `투표 상세 페이지 조회 테스트`() {
        //given
        val voteSize = 3
        val saveDummyVotes = saveDummyVotesDetail(voteSize) // voteSize 가 3인 경우, voteAmount = [6, 4, 2]
        val findVote = saveDummyVotes[0]

        //when
        em.clear()
        val voteVo = voteQuerydslRepository.findVoteById(findVote.id)!!

        //then
        assertThat(voteVo.vote.title).isEqualTo(findVote.title)
        assertThat(voteVo.vote.voteOptions[0].text).contains("OptionA")
        assertThat(voteVo.vote.voteOptions[1].text).contains("OptionB")
    }


    //test용 투표 저장
    private fun saveDummyVotes(amount: Int): MutableList<Vote> {
        val member = EntityFactory.testMemberA()
        memberRepository.save(member)
        val sampleVotes: MutableList<Vote> = mutableListOf()
        for (i in 1..amount) {
            sampleVotes.add(Vote("Vote$i", JobCategory.DEVELOPER, "Content$i", VoteType.TEXT, createdBy = member))
        }

        return voteRepository.saveAll(sampleVotes)
    }

    private fun saveDummyVotesDetail(amount: Int): MutableList<Vote> {
        val memberA = EntityFactory.testMemberA()
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

