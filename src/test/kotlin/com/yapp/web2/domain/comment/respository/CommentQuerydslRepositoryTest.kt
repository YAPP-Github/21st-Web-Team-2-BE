package com.yapp.web2.domain.comment.respository

import com.yapp.web2.common.EntityFactory
import com.yapp.web2.domain.comment.model.Comment
import com.yapp.web2.domain.like.model.CommentLikes
import com.yapp.web2.domain.topic.model.TopicCategory
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.topic.model.Topic
import com.yapp.web2.domain.topic.model.VoteType
import com.yapp.web2.domain.topic.repository.TopicRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@SpringBootTest
internal class CommentQuerydslRepositoryTest @Autowired constructor(
    val commentRepository: CommentRepository,
    val commentQuerydslRepository: CommentQuerydslRepository,
    val memberRepository: MemberRepository,
    val topicRepository: TopicRepository,
) {

    @BeforeAll
    fun saveTestData() {
        saveDummyComments()
    }


    @Test
    fun `댓글 조회 테스트`() {
        //when
        val findCommentsSlice = commentQuerydslRepository.findComments(1L)

        //then
        assertThat(findCommentsSlice.content).hasSize(10)

        assertThat(findCommentsSlice.content[0].createdBy.nickname).isEqualTo("MemberA")
        assertThat(findCommentsSlice.hasNext()).isTrue
    }

    @Test
    fun `댓글 마지막 offset 조회 테스트`() {

        val lastCommentId = 6L // lastCommentId 6L => id: 5, 4, 3, 2, 1만 조회됨

        //when
        val findCommentsSlice = commentQuerydslRepository.findComments(1L, lastCommentId)

        //then
        assertThat(findCommentsSlice.content).hasSize(5)
        assertThat(findCommentsSlice.hasNext()).isFalse
    }


    // voteId == 1인 투표 게시글에 대한 댓글 30개를 저장합니다.
    // 댓글에 좋아요는 (30 - id) +1 만큼 추가됩니다. ex) [id: 1, likeAmount: 30], [id: 2, likeAmount: 29], ... [id: 30, likeAmount: 1]
    private fun saveDummyComments() {
        val member = memberRepository.save(
            EntityFactory.testMemberA()
        )

        val topic = topicRepository.save(
            Topic("VoteA", TopicCategory.DEVELOPER, "ContentA", VoteType.TEXT, createdBy = member)
        )

        val sampleComments: MutableList<Comment> = mutableListOf()
        for (i in 1..30) {
            sampleComments.add(Comment(member, "Comment $i", topic))
        }

        for (i in 0 until sampleComments.size) {
            val comment = sampleComments[i]
            for (j in i + 1 downTo 1) {
                val commentLikes = CommentLikes(member, comment)
                comment.addCommentLikes(commentLikes)
            }
        }
        commentRepository.saveAll(sampleComments)
    }
}
