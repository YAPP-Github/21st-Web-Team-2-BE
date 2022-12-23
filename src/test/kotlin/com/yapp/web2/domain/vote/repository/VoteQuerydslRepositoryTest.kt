package com.yapp.web2.domain.vote.repository

import com.yapp.web2.domain.member.model.JobCategory
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.vote.model.Vote
import com.yapp.web2.domain.vote.model.VoteType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
internal class VoteQuerydslRepositoryTest @Autowired constructor(
    val voteRepository: VoteRepository,
    val voteQuerydslRepository: VoteQuerydslRepository,
    val memberRepository: MemberRepository,
) {
    @Test
    fun `투표 페이징 조회 테스트`() {
        saveVotes(30)

        val searchBySlice = voteQuerydslRepository.searchBySlice(pageable = Pageable.ofSize(3))
        val content = searchBySlice?.content
        assertThat(content).hasSize(3)
        assertThat(content!![0].title).isEqualTo("Vote30")
        assertThat(searchBySlice.hasNext()).isTrue
    }

    @Test
    fun `투표 페이징 인덱스 조회 테스트`() {
        saveVotes(30)

        // 최신순 -> id: 30, 29, 28, ... ,2, 1 순으로 정렬됨
        // id가 3번 이후 부터 size = 3으로 slice
        val searchBySlice = voteQuerydslRepository.searchBySlice(3, Pageable.ofSize(3))
        val content = searchBySlice?.content
        assertThat(content).hasSize(2)
        assertThat(searchBySlice!!.hasNext()).isFalse
    }

    //test용 투표 저장
    private fun saveVotes(amount: Int) {
        val member = Member("MemberA", JobCategory.DEVELOPER, 3)
        memberRepository.save(member)
        val sampleVotes: MutableList<Vote> = mutableListOf()
        for (i in 1..amount) {
            sampleVotes.add(Vote("Vote$i", JobCategory.DEVELOPER, "Content$i", VoteType.TEXT, createdBy = member))
        }
        voteRepository.saveAll(sampleVotes)
    }

}

