package com.yapp.web2.domain.vote.model

import com.yapp.web2.common.EntityFactory
import com.yapp.web2.common.entity.EntityStatus
import com.yapp.web2.domain.member.model.JobCategory
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.vote.repository.VoteRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
internal class VoteTest(
    @Autowired val voteRepository: VoteRepository,
    @Autowired val memberRepository: MemberRepository,
) {
    private lateinit var vote: Vote

    @BeforeEach
    fun beforeEach() {
        memberRepository.deleteAll()
        voteRepository.deleteAll()

        val member = EntityFactory.testMemberA()
        vote = Vote("VoteA", JobCategory.DEVELOPER, "ContentA", VoteType.TEXT, createdBy = member)
        memberRepository.save(member)
        voteRepository.save(vote)
    }

    @Test
    fun `소프트 딜리트된 엔티티는 조회되지 않는다`() {
        vote.softDelete()
        voteRepository.save(vote)

        val votes = voteRepository.findAll()

        assertThat(votes).hasSize(0)
    }
}
