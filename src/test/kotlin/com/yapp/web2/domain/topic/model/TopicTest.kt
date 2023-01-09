package com.yapp.web2.domain.topic.model

import com.yapp.web2.domain.member.model.JobCategory
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.topic.repository.TopicRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
internal class TopicTest(
    @Autowired val topicRepository: TopicRepository,
    @Autowired val memberRepository: MemberRepository,
) {
    private lateinit var topic: Topic

    @BeforeEach
    fun beforeEach() {
        memberRepository.deleteAll()
        topicRepository.deleteAll()

        val member = Member("MemberA", JobCategory.DEVELOPER, 3, null)
        topic = Topic("VoteA", JobCategory.DEVELOPER, "ContentA", VoteType.TEXT, createdBy = member)
        memberRepository.save(member)
        topicRepository.save(topic)
    }

    @Test
    fun `소프트 딜리트된 엔티티는 조회되지 않는다`() {
        topic.softDelete()
        topicRepository.save(topic)

        val topics = topicRepository.findAll()

        assertThat(topics).hasSize(0)
    }
}
