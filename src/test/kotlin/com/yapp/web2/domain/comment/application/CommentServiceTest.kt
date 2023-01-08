package com.yapp.web2.domain.comment.application

import com.yapp.web2.common.EntityFactory
import com.yapp.web2.domain.comment.model.Comment
import com.yapp.web2.domain.comment.respository.CommentRepository
import com.yapp.web2.domain.like.model.CommentLikes
import com.yapp.web2.domain.member.model.JobCategory
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.vote.model.Vote
import com.yapp.web2.domain.vote.model.VoteType
import com.yapp.web2.domain.vote.repository.VoteRepository
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
internal class CommentServiceTest @Autowired constructor(
    val commentService: CommentService,
    val voteRepository: VoteRepository,
    val memberRepository: MemberRepository,
    val commentRepository: CommentRepository,
) {

    @BeforeAll
    fun saveTestData() {
        saveDummyComments()
    }

    @Test
    fun `댓글 최신순 조회 테스트`() {
        //when
        val latestCommentSlice = commentService.getLatestComments(1L, null)

        //then
        assertThat(latestCommentSlice.hasNext()).isTrue

        val comments = latestCommentSlice.content
        assertThat(comments).hasSize(10)
        assertThat(comments[0].offsetId).isEqualTo(30L)
        assertThat(comments[0].likeAmount).isEqualTo(30)
    }


    // voteId == 1인 투표 게시글에 대한 댓글 30개를 저장합니다.
    // 댓글에 좋아요는 (30 - id) +1 만큼 추가됩니다. ex) [id: 1, likeAmount: 30], [id: 2, likeAmount: 29], ... [id: 30, likeAmount: 1]
    private fun saveDummyComments() {
        val member = memberRepository.save(
            EntityFactory.testMemberA()
        )

        val vote = voteRepository.save(
            Vote("VoteA", JobCategory.DEVELOPER, "ContentA", VoteType.TEXT, createdBy = member)
        )

        val sampleComments: MutableList<Comment> = mutableListOf()
        for (i in 1..30) {
            sampleComments.add(Comment(member, "Comment $i", vote))
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
