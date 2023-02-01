package com.yapp.web2.domain.topic.application

import com.yapp.web2.common.EntityFactory
import com.yapp.web2.domain.like.model.TopicLikes
import com.yapp.web2.domain.topic.model.TopicCategory
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.topic.model.Topic
import com.yapp.web2.domain.topic.model.VoteType
import com.yapp.web2.domain.topic.model.option.VoteOption
import com.yapp.web2.domain.topic.model.option.VoteOptionMember
import com.yapp.web2.domain.topic.repository.TopicRepository
import com.yapp.web2.web.api.error.BusinessException
import com.yapp.web2.web.dto.topic.request.TopicLikePostRequest
import com.yapp.web2.web.dto.topic.request.TopicPostRequest
import com.yapp.web2.web.dto.voteoption.request.VoteOptionPostRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class TopicServiceTest @Autowired constructor(
    val topicService: TopicService,
    val topicRepository: TopicRepository,
    val memberRepository: MemberRepository,
) {

    @BeforeEach
    fun beforeEach() {
        memberRepository.deleteAll()
        topicRepository.deleteAll()
    }


    @Test
    fun `최신순 페이지 조회 테스트`() {
        //given
        saveDummyTopicsDetail(2)

        //when
        val latestTopicsSlice = topicService.getLatestTopicsSlice(null, null).content

        //then
        assertThat(latestTopicsSlice).hasSize(2)

        val voteOptionPreview = latestTopicsSlice[0]
        assertThat(voteOptionPreview.voteAmount).isEqualTo(3)

        assertThat(voteOptionPreview.voteOptions[0].voteAmount).isEqualTo(2)
        assertThat(voteOptionPreview.voteOptions[1].voteAmount).isEqualTo(1)
    }

    @Test
    fun `최신순 페이지 조회 카테고리 필터 테스트`() {
        //given
        val topics = saveDummyTopicsDetail(2)
        val createdBy = topics[0].createdBy
        topicRepository.saveAll(listOf(
            Topic("Topic CareerA", TopicCategory.CAREER, "Content CareerA", VoteType.TEXT, createdBy = createdBy),
            Topic("Topic CareerB", TopicCategory.CAREER, "Content CareerB", VoteType.TEXT, createdBy = createdBy),
            Topic("Topic CareerC", TopicCategory.CAREER, "Content CareerC", VoteType.TEXT, createdBy = createdBy),
        ))

        //when
        val latestTopicsSlice = topicService.getLatestTopicsSlice(null, TopicCategory.CAREER).content

        //then
        assertThat(latestTopicsSlice).hasSize(3)
    }

    @Test
    fun `인기순 투표 게시글 조회 테스트`() {
        //given
        saveDummyTopicsDetailWithVoteAmount(10)

        //when
        val popularTopics = topicService.getPopularTopics()

        //then
        assertThat(popularTopics).hasSize(4)

        val voteOptionPreview = popularTopics[0]
        assertThat(voteOptionPreview.voteAmount).isEqualTo(20)
    }

    @Test
    fun `투표 게시글 상세 조회 테스트`() {
        //given
        val dummyTopics = saveDummyTopicsDetailWithVoteAmount(10)

        //when
        val findVote = dummyTopics[0]
        val voteDetail = topicService.getTopicDetail(findVote.id)

        //then
        assertThat(voteDetail.title).isEqualTo(findVote.title)
    }

    @Test
    fun `존재하지 않는 투표 게시글 상세 조회시 예외 발생`() {
        //given
        val invalidVoteId = 400000L

        assertThatThrownBy { topicService.getTopicDetail(invalidVoteId) }
            .isInstanceOf(BusinessException::class.java)
    }

    @Test
    fun `투표 게시글 저장 테스트`() {
        //given
        val testMemberA = EntityFactory.testMemberA()
        memberRepository.save(testMemberA)
        val topicPostRequest = TopicPostRequest(
            "TopicA",
            "Contents A",
            listOf(
                VoteOptionPostRequest("OptionA", null, null),
                VoteOptionPostRequest("OptionB", null, null),
            ),
            TopicCategory.DEVELOPER,
            listOf("tagA", "tagB")
        )

        //when
        topicService.saveTopic(testMemberA, topicPostRequest)

        //then
        val findOne = topicRepository.findAll()[0]
        assertThat(findOne.title).isEqualTo("TopicA")
        assertThat(findOne.voteType).isEqualTo(VoteType.TEXT)
    }

    @Test
    fun `투표 게시글 저장시, 데이터가 누락된 경우 예외 발생`() {
        //given
        val testMemberA = EntityFactory.testMemberA()
        memberRepository.save(testMemberA)
        val topicPostRequest = TopicPostRequest(
            null, // 예외 케이스
            "Contents A",
            listOf(
                VoteOptionPostRequest("OptionA", null, null),
                VoteOptionPostRequest("OptionB", null, null),
            ),
            TopicCategory.DEVELOPER,
            listOf("tagA", "tagB")
        )

        //when
        assertThatThrownBy { topicService.saveTopic(testMemberA, topicPostRequest) }
            .isInstanceOf(BusinessException::class.java)
    }

    @Test
    fun `투표 좋아요 테스트`() {
        //given
        val testMemberA = EntityFactory.testMemberA()
        memberRepository.save(testMemberA)
        val testTopicA = EntityFactory.testTopicA(testMemberA)
        topicRepository.save(testTopicA)

        //when
        val toggleTopicLikes = topicService.toggleTopicLikes(testMemberA, TopicLikePostRequest(testTopicA.id))

        //then
        assertThat(toggleTopicLikes.liked).isTrue
    }

    @Test
    fun `투표 좋아요 해제 테스트`() {
        //given
        val testMemberA = EntityFactory.testMemberA()
        memberRepository.save(testMemberA)
        val testTopicA = EntityFactory.testTopicA(testMemberA)
        testTopicA.addLikeTopic(TopicLikes(testMemberA, testTopicA))
        topicRepository.save(testTopicA)

        //when
        val toggleTopicLikes = topicService.toggleTopicLikes(testMemberA, TopicLikePostRequest(testTopicA.id))

        //then
        assertThat(toggleTopicLikes.liked).isFalse
    }




    private fun saveDummyTopicsDetail(amount: Int): MutableList<Topic> {
        // 유저 생성
        val memberA = EntityFactory.testMemberA()
        val memberB = EntityFactory.testMemberB()
        val memberC = EntityFactory.testMemberC()
        memberRepository.saveAll(listOf(memberA, memberB, memberC))

        // 투표 게시글 생성
        val sampleTopics: MutableList<Topic> = mutableListOf()
        for (i in 1..amount) {
            sampleTopics.add(Topic("Vote$i", TopicCategory.DEVELOPER, "Content$i", VoteType.TEXT, createdBy = memberA))
        }

        // 투표 게시글 마다 2개의 옵션이 존재
        // 첫번째 옵션에 2개, 두번째 옵션에 1개가 투표됨
        for (vote in sampleTopics) {
            val voteOptionA = VoteOption("${vote.contents} OptionA", null, null, vote)
            val voteOptionB = VoteOption("${vote.contents} OptionB", null, null, vote)

            vote.addVoteOption(voteOptionA)
            vote.addVoteOption(voteOptionB)

            voteOptionA.addVoteOptionMember(VoteOptionMember(memberA, voteOptionA))
            voteOptionA.addVoteOptionMember(VoteOptionMember(memberB, voteOptionA))
            voteOptionB.addVoteOptionMember(VoteOptionMember(memberC, voteOptionB))
        }


        return topicRepository.saveAll(sampleTopics)
    }

    private fun saveDummyTopicsDetailWithVoteAmount(amount: Int): MutableList<Topic> {
        val memberA = EntityFactory.testMemberA()
        memberRepository.saveAll(listOf(memberA))

        val sampleTopics: MutableList<Topic> = mutableListOf()
        for (i in 1..amount) {
            sampleTopics.add(Topic("Vote$i", TopicCategory.DEVELOPER, "Content$i", VoteType.TEXT, createdBy = memberA))
        }

        for (vote in sampleTopics) {
            vote.addVoteOption(VoteOption("${vote.contents} OptionA", null, null, vote))
            vote.addVoteOption(VoteOption("${vote.contents} OptionB", null, null, vote))
        }

        // 투표 게시글 크기의 2배 만큼 투표수를 받음
        // ex) TopicSize = 10, 게시글의 투표수는 20, 18, 16, ... 씩 줄어듦
        for (i in 0 until sampleTopics.size) {
            val voteOptionA = sampleTopics[i].voteOptions[0]
            val voteOptionB = sampleTopics[i].voteOptions[1]

            for (j in 0..i) {
                voteOptionA.addVoteOptionMember(VoteOptionMember(memberA, voteOptionA))
                voteOptionB.addVoteOptionMember(VoteOptionMember(memberA, voteOptionB))
            }
        }
        return topicRepository.saveAll(sampleTopics)
    }
}


