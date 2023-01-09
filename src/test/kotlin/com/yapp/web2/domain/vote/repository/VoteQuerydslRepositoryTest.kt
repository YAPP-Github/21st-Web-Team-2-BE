package com.yapp.web2.domain.vote.repository

import com.yapp.web2.common.EntityFactory
import com.yapp.web2.domain.member.model.JobCategory
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.topic.model.Topic
import com.yapp.web2.domain.topic.model.VoteType
import com.yapp.web2.domain.topic.model.option.VoteOption
import com.yapp.web2.domain.topic.model.option.VoteOptionMember
import com.yapp.web2.domain.topic.repository.TopicQuerydslRepository
import com.yapp.web2.domain.topic.repository.TopicRepository
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
internal class VoteQuerydslRepositoryTest @Autowired constructor(
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
        val dummyVoteAmount = 20
        val pageSize = 6

        val dummyVotes = saveDummyTopics(dummyVoteAmount)
        dummyVotes.sortByDescending { it.createdAt }

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
        val dummyVoteAmount = 20

        val dummyVotes = saveDummyTopics(dummyVoteAmount)
        dummyVotes.sortByDescending { it.createdAt }
        val lastVoteId = dummyVotes[(dummyVoteAmount - 1) - 2].id // 마지막에서 2번째 부터 페이징 조회

        //when
        //우선순위(최신순)으로 정렬된 데이터에서, id가 lastVoteId 이후에서 부터 조회
        val searchBySlice = topicQuerydslRepository.findLatestTopicsByCategory(lastVoteId)

        //then
        val content = searchBySlice.topics
        assertThat(content).hasSize(2)
        assertThat(searchBySlice.hasNext).isFalse
    }

    @Test
    fun `투표 카테고리 필터 조회 테스트`() {
        //given
        val dummyVoteAmount = 3
        saveDummyTopics(dummyVoteAmount) // JobCategory == DEVELOPER 인 게시글 3개 저장

        val memberB = EntityFactory.testMemberB()
        memberRepository.save(memberB)
        val sampleVotes: MutableList<Topic> = mutableListOf()
        for (i in 1..3) {
            sampleVotes.add(Topic("Vote$i", JobCategory.DESIGNER, "Content$i", VoteType.TEXT, createdBy = memberB))
        }
        topicRepository.saveAll(sampleVotes) // JobCategory == DESIGNER 인 게시글 3개 저장

        //when
        //우선순위(최신순)으로 정렬된 데이터에서, id가 lastVoteId 이후에서 부터 조회
        val searchBySlice = topicQuerydslRepository.findLatestTopicsByCategory(jobCategory = JobCategory.DEVELOPER)

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
        val voteSize = 6
        saveDummyTopicsDetail(voteSize) // voteSize 가 6인 경우, voteAmount = [12, 10, 8, 6]

        val expectedVoteAmounts = mutableListOf<Long>()
        for (i in 0..3) {
            expectedVoteAmounts.add((voteSize - i) * 2.toLong())
        }

        //when
        val findPopularVotes = topicQuerydslRepository.findPopularTopics()

        //then
        assertThat(findPopularVotes).hasSize(4)
        assertThat(findPopularVotes).extracting("voteAmount").isEqualTo(expectedVoteAmounts)
    }

    @Test
    fun `투표 상세 페이지 조회 테스트`() {
        //given
        val voteSize = 3
        val saveDummyVotes = saveDummyTopicsDetail(voteSize) // voteSize 가 3인 경우, voteAmount = [6, 4, 2]
        val findVote = saveDummyVotes[0]

        //when
        em.clear()
        val voteVo = topicQuerydslRepository.findTopicDetailById(findVote.id)!!

        //then
        assertThat(voteVo.topic.title).isEqualTo(findVote.title)
        assertThat(voteVo.topic.voteOptions[0].text).contains("OptionA")
        assertThat(voteVo.topic.voteOptions[1].text).contains("OptionB")
    }


    //test용 투표 저장
    private fun saveDummyTopics(amount: Int): MutableList<Topic> {
        val member = EntityFactory.testMemberA()
        memberRepository.save(member)
        val sampleVotes: MutableList<Topic> = mutableListOf()
        for (i in 1..amount) {
            sampleVotes.add(Topic("Vote$i", JobCategory.DEVELOPER, "Content$i", VoteType.TEXT, createdBy = member))
        }

        return topicRepository.saveAll(sampleVotes)
    }

    private fun saveDummyTopicsDetail(amount: Int): MutableList<Topic> {
        val memberA = EntityFactory.testMemberA()
        memberRepository.saveAll(listOf(memberA))

        val sampleVotes: MutableList<Topic> = mutableListOf()
        for (i in 1..amount) {
            sampleVotes.add(Topic("Vote$i", JobCategory.DEVELOPER, "Content$i", VoteType.TEXT, createdBy = memberA))
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
        return topicRepository.saveAll(sampleVotes)
    }

}

