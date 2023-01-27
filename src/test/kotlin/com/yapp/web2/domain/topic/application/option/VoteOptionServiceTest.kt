package com.yapp.web2.domain.topic.application.option

import com.yapp.web2.common.EntityFactory
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.topic.model.option.VoteOptionMember
import com.yapp.web2.domain.topic.repository.TopicRepository
import com.yapp.web2.domain.topic.repository.option.VoteOptionMemberRepository
import com.yapp.web2.web.dto.voteoption.request.VotePostRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@SpringBootTest
internal class VoteOptionServiceTest @Autowired constructor(
    val topicRepository: TopicRepository,
    val memberRepository: MemberRepository,
    val voteOptionService: VoteOptionService,
    val voteOptionMemberRepository: VoteOptionMemberRepository,
) {

    private final val testMemberA = EntityFactory.testMemberA()
    private final val topicA = EntityFactory.testTopicA(testMemberA)

    @BeforeAll
    fun saveData() {
        memberRepository.save(testMemberA)
        topicRepository.save(topicA)
    }

    @BeforeEach
    fun deleteAll() {
        voteOptionMemberRepository.deleteAll()
    }

    @Test
    fun `동일한 투표이력이 존재하는 경우 삭제`() {
        //given
        val voteOption = topicA.voteOptions[0]
        voteOptionMemberRepository.save(
            VoteOptionMember(testMemberA, voteOption)
        )

        val votePostRequest = VotePostRequest(topicA.id, voteOption.id)

        //when
        voteOptionService.vote(testMemberA, votePostRequest)

        //then
        val findAll = voteOptionMemberRepository.findAll()
        assertThat(findAll).hasSize(0)
    }

    @Test
    fun `동일한 투표 게시글에 다른 투표 이력이 존재하는 경우 변경`() {
        //given
        voteOptionMemberRepository.save(
            VoteOptionMember(testMemberA, topicA.voteOptions[0])
        )

        val voteOption = topicA.voteOptions[1]
        val votePostRequest = VotePostRequest(topicA.id, voteOption.id)

        //when
        voteOptionService.vote(testMemberA, votePostRequest)

        //then
        val findAll = voteOptionMemberRepository.findAll()
        assertThat(findAll).hasSize(1)
        assertThat(findAll[0].voteOption.id).isEqualTo(voteOption.id)
    }

    @Test
    fun `투표 게시글에 이력이 없는 경우 추가`() {
        //given
        val voteOption = topicA.voteOptions[0]
        val votePostRequest = VotePostRequest(topicA.id, voteOption.id)

        //when
        voteOptionService.vote(testMemberA, votePostRequest)

        //then
        val findAll = voteOptionMemberRepository.findAll()
        assertThat(findAll).hasSize(1)
        assertThat(findAll[0].voteOption.id).isEqualTo(voteOption.id)
    }
}
