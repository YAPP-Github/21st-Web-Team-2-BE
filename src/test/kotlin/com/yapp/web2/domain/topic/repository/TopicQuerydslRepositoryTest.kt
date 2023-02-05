package com.yapp.web2.domain.topic.repository

import com.yapp.web2.common.EntityFactory
import com.yapp.web2.domain.topic.model.TopicCategory
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.topic.model.Topic
import com.yapp.web2.domain.topic.model.VoteType
import com.yapp.web2.domain.topic.model.option.VoteOption
import com.yapp.web2.domain.topic.model.option.VoteOptionMember
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
internal class TopicQuerydslRepositoryTest @Autowired constructor(
    val topicRepository: TopicRepository,
    val memberRepository: MemberRepository,
    val topicQuerydslRepository: TopicQuerydslRepository,
) {
    @PersistenceUnit
    lateinit var emf: EntityManagerFactory

    @PersistenceContext
    lateinit var em: EntityManager

    @BeforeEach
    fun beforeEach() {
        memberRepository.deleteAll()
        topicRepository.deleteAll()
    }

    @Test
    fun `투표 페이징 조회 테스트`() {
        //given
        val dummyTopicAmount = 20
        val pageSize = 6

        val dummyTopics = saveDummyTopics(dummyTopicAmount)
        dummyTopics.sortByDescending { it.createdAt }

        //when
        val searchBySlice = topicQuerydslRepository.findLatestTopicsByCategory()

        //then
        val content = searchBySlice.topics
        assertThat(content).hasSize(pageSize)
        assertThat(searchBySlice.hasNext).isTrue
    }

    @Test
    fun `투표 페이징 인덱스 조회 테스트`() {
        //given
        val dummyTopicAmount = 20

        val dummyTopics = saveDummyTopics(dummyTopicAmount)
        dummyTopics.sortByDescending { it.createdAt }
        val lastTopicId = dummyTopics[(dummyTopicAmount - 1) - 2].id // 마지막에서 2번째 부터 페이징 조회

        //when
        //우선순위(최신순)으로 정렬된 데이터에서, id가 lastTopicId 이후에서 부터 조회
        val searchBySlice = topicQuerydslRepository.findLatestTopicsByCategory(lastTopicId)

        //then
        val content = searchBySlice.topics
        assertThat(content).hasSize(2)
        assertThat(searchBySlice.hasNext).isFalse
    }

    @Test
    fun `투표 카테고리 필터 조회 테스트`() {
        //given
        val dummyTopicAmount = 3
        saveDummyTopics(dummyTopicAmount) // JobCategory == DEVELOPER 인 게시글 3개 저장

        val memberB = EntityFactory.testMemberB()
        memberRepository.save(memberB)
        val sampleTopics: MutableList<Topic> = mutableListOf()
        for (i in 1..3) {
            sampleTopics.add(Topic("Vote$i", TopicCategory.DESIGNER, "Content$i", VoteType.TEXT, createdBy = memberB))
        }
        topicRepository.saveAll(sampleTopics) // JobCategory == DESIGNER 인 게시글 3개 저장

        //when
        //우선순위(최신순)으로 정렬된 데이터에서, id가 lastTopicId 이후에서 부터 조회
        val searchBySlice = topicQuerydslRepository.findLatestTopicsByCategory(topicCategory = TopicCategory.DEVELOPER)

        //then
        val content = searchBySlice.topics
        assertThat(content).hasSize(3)
        assertThat(searchBySlice.hasNext).isFalse
    }

    @Test
    fun `투표 페이징 멤버 페치 조인 테스트`() {
        //given
        saveDummyTopics(10)

        //when
        val searchBySlice = topicQuerydslRepository.findLatestTopicsByCategory()

        //then
        val content = searchBySlice.topics
        val loaded = emf.persistenceUnitUtil.isLoaded(content[0].topic.createdBy)
        assertThat(loaded).isTrue
    }

    @Test
    fun `투표 개수 정렬 조회 테스트`() {
        //given
        val topicSize = 6
        saveDummyTopicsDetail(topicSize) // topicSize 가 6인 경우, voteAmount = [12, 10, 8, 6]

        val expectedVoteAmounts = mutableListOf<Long>()
        for (i in 0..3) {
            expectedVoteAmounts.add((topicSize - i) * 2.toLong())
        }

        //when
        val findPopularTopics = topicQuerydslRepository.findPopularTopics()

        //then
        assertThat(findPopularTopics).hasSize(4)
        assertThat(findPopularTopics).extracting("voteAmount").isEqualTo(expectedVoteAmounts)
    }

    @Test
    fun `투표 상세 페이지 조회 테스트`() {
        //given
        val topicSize = 3
        val saveDummyTopics = saveDummyTopicsDetail(topicSize) // voteSize 가 3인 경우, voteAmount = [6, 4, 2]
        val findTopic = saveDummyTopics[0]

        //when
        em.clear()
        val topicVo = topicQuerydslRepository.findTopicDetailById(findTopic.id)!!

        //then
        assertThat(topicVo.topic.title).isEqualTo(findTopic.title)
        assertThat(topicVo.topic.voteOptions[0].text).contains("OptionA")
        assertThat(topicVo.topic.voteOptions[1].text).contains("OptionB")
    }


    //test용 투표 저장
    private fun saveDummyTopics(amount: Int): MutableList<Topic> {
        val memberA = EntityFactory.testMemberA()
        memberRepository.save(memberA)
        val sampleTopics: MutableList<Topic> = mutableListOf()
        for (i in 1..amount) {
            sampleTopics.add(Topic("Vote$i", TopicCategory.DEVELOPER, "Content$i", VoteType.TEXT, createdBy = memberA))
        }

        return topicRepository.saveAll(sampleTopics)
    }

    private fun saveDummyTopicsDetail(amount: Int): MutableList<Topic> {
        val memberA = EntityFactory.testMemberA()
        memberRepository.saveAll(listOf(memberA))

        val sampleTopics: MutableList<Topic> = mutableListOf()
        for (i in 1..amount) {
            sampleTopics.add(Topic("Vote$i", TopicCategory.DEVELOPER, "Content$i", VoteType.TEXT, createdBy = memberA))
        }

        for (topic in sampleTopics) {
            topic.addVoteOption(VoteOption("${topic.contents} OptionA", null, null, null, topic))
            topic.addVoteOption(VoteOption("${topic.contents} OptionB", null, null, null, topic))
        }

        // 투표 게시글 크기의 2배 만큼 투표수를 받음
        // ex) topicSize = 10, 게시글의 투표수는 20, 18, 16, ... 씩 줄어듦
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

