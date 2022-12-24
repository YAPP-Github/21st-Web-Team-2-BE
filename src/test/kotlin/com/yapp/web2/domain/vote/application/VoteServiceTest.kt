package com.yapp.web2.domain.vote.application

import com.yapp.web2.domain.member.model.JobCategory
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.vote.model.Vote
import com.yapp.web2.domain.vote.model.VoteType
import com.yapp.web2.domain.vote.repository.VoteRepository
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
    fun `메인페이지 조회 테스트`() {
        saveDummyVotes(20)
        println("======================start======================")
        val votesByPageRequest = voteService.getVotesByPageRequest(null, PageRequest.of(0, 5))
        println("======================end======================")
        for (votePreviewResponse in votesByPageRequest) {
            println(votePreviewResponse)
        }
    }

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


