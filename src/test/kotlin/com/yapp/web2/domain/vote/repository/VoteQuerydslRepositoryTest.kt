package com.yapp.web2.domain.vote.repository

import com.yapp.web2.domain.member.model.JobCategory
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.vote.model.Vote
import com.yapp.web2.domain.vote.model.VoteType
import com.yapp.web2.domain.vote.model.option.VoteOption
import com.yapp.web2.domain.vote.model.option.VoteOptionMember
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.PersistenceUnit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.util.Comparator

@Transactional
@SpringBootTest
internal class VoteQuerydslRepositoryTest @Autowired constructor(
    val voteRepository: VoteRepository,
    val memberRepository: MemberRepository,
    val voteQuerydslRepository: VoteQuerydslRepository,
) {

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
        val searchBySlice = voteQuerydslRepository.findLatestVotes()

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
        val searchBySlice = voteQuerydslRepository.findLatestVotes(lastVoteId)

        //then
        val content = searchBySlice.votes
        assertThat(content).hasSize(2)
        assertThat(searchBySlice.hasNext).isFalse
    }

    @PersistenceUnit
    lateinit var emf: EntityManagerFactory

    @Test
    fun `투표 페이징 멤버 페치 조인 테스트`() {
        //given
        saveDummyVotes(10)

        //when
        val searchBySlice = voteQuerydslRepository.findLatestVotes()

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

    //test용 투표 저장
    private fun saveDummyVotes(amount: Int): MutableList<Vote> {
        val member = Member("MemberA", JobCategory.DEVELOPER, 3)
        memberRepository.save(member)
        val sampleVotes: MutableList<Vote> = mutableListOf()
        for (i in 1..amount) {
            sampleVotes.add(Vote("Vote$i", JobCategory.DEVELOPER, "Content$i", VoteType.TEXT, createdBy = member))
        }

        return voteRepository.saveAll(sampleVotes)
    }

    private fun saveDummyVotesDetail(amount: Int) {
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
        voteRepository.saveAll(sampleVotes)
    }

}

