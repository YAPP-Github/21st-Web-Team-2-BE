package com.yapp.web2.domain.vote.repository

import com.yapp.web2.domain.member.model.JobCategory
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.vote.model.Vote
import com.yapp.web2.domain.vote.model.VoteType
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.PersistenceUnit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional

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
        val searchBySlice = voteQuerydslRepository.findMainPageVotes(pageable = Pageable.ofSize(pageSize))

        //then
        val content = searchBySlice.content
        assertThat(content).hasSize(pageSize)
        assertThat(searchBySlice.hasNext()).isTrue
    }

    @Test
    fun `투표 페이징 인덱스 조회 테스트`() {
        //given
        val dummyVoteAmount = 20
        val pageSize = 5

        val dummyVotes = saveDummyVotes(dummyVoteAmount)
        dummyVotes.sortByDescending { it.createdAt }
        val lastVoteId = dummyVotes[(dummyVoteAmount - 1) - 2].id

        //when
        //우선순위(최신순)으로 정렬된 데이터에서, id가 lastVoteId 이후에서 부터 조회
        val searchBySlice = voteQuerydslRepository.findMainPageVotes(lastVoteId, Pageable.ofSize(pageSize))

        //then
        val content = searchBySlice.content
        assertThat(content).hasSize(2)
        assertThat(searchBySlice.hasNext()).isFalse
    }

    @PersistenceUnit
    lateinit var emf: EntityManagerFactory

    @Test
    fun `투표 페이징 멤버 페치 조인 테스트`() {
        //given
        saveDummyVotes(10)
        val pageSize = 5

        //when
        val searchBySlice = voteQuerydslRepository.findMainPageVotes(pageable = Pageable.ofSize(pageSize))

        //then
        val content = searchBySlice.content
        val loaded = emf.persistenceUnitUtil.isLoaded(content[0].createdBy)
        assertThat(loaded).isTrue
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

}

