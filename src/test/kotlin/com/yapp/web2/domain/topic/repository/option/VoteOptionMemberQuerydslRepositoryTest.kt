package com.yapp.web2.domain.topic.repository.option

import com.yapp.web2.common.EntityFactory
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.topic.model.Topic
import com.yapp.web2.domain.topic.model.option.VoteOptionMember
import com.yapp.web2.domain.topic.repository.TopicRepository
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
internal class VoteOptionMemberQuerydslRepositoryTest @Autowired constructor(
    val topicRepository: TopicRepository,
    val memberRepository: MemberRepository,
    val voteOptionMemberRepository: VoteOptionMemberRepository,
    val voteOptionMemberQuerydslRepository: VoteOptionMemberQuerydslRepository,
) {
    @Test
    fun `투표게시글 투표 이력 조회 테스트`() {
        //given
        val testMemberA = EntityFactory.testMemberA()
        memberRepository.save(testMemberA)

        val topicA = EntityFactory.testTopicA(testMemberA)
        topicRepository.save(topicA)

        voteOptionMemberRepository.save(
            VoteOptionMember(testMemberA, topicA.voteOptions[0])
        )

        //when
        val findOne = voteOptionMemberQuerydslRepository.findByMemberAndTopic(testMemberA, topicA)

        //then
        assertThat(findOne).isNotNull
    }

}
