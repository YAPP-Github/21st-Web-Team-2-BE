package com.yapp.web2.web.api.controller.comment

import com.yapp.web2.domain.comment.model.Comment
import com.yapp.web2.domain.comment.respository.CommentRepository
import com.yapp.web2.domain.like.model.CommentLikes
import com.yapp.web2.domain.member.model.JobCategory
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.vote.model.Vote
import com.yapp.web2.domain.vote.model.VoteType
import com.yapp.web2.domain.vote.repository.VoteRepository
import com.yapp.web2.web.api.controller.ApiControllerTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CommentControllerTest @Autowired constructor(
    val voteRepository: VoteRepository,
    val memberRepository: MemberRepository,
    val commentRepository: CommentRepository,
) : ApiControllerTest(uri = "/api/v1/comment") {

    @BeforeAll
    fun saveTestData() {
        saveDummyComments()
    }

    @Test
    fun `getCommentsNoOffsetTest`() {
        val findVoteId = 1L
        val uri = "$uri/{voteId}/latest"
        mockMvc.perform(
            RestDocumentationRequestBuilders.get(uri, findVoteId)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("SUCCESS"))
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                MockMvcRestDocumentation.document(
                    "get-comments",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    RequestDocumentation.pathParameters(
                        RequestDocumentation.parameterWithName("voteId").description("투표 게시글 Id")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.beneathPath("data").withSubsectionId("data"),
                        *commentDataResponseFieldSnippet(),
                    )
                ),
            )
    }

    private fun commentDataResponseFieldSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            PayloadDocumentation.fieldWithPath("commentId").description("댓글 Id"),
            PayloadDocumentation.fieldWithPath("createdMemberId").description("댓글 작성자 Id"),
            PayloadDocumentation.fieldWithPath("createdMemberName").description("댓글 작성자 닉네임"),
            PayloadDocumentation.fieldWithPath("createdMemberProfileImage").description("댓글 작성자 프로필 이미지"),
            PayloadDocumentation.fieldWithPath("createdMemberJobCategory").description("댓글 작성자 직군 분야"),
            PayloadDocumentation.fieldWithPath("createdMemberWorkingYears").description("댓글 작성자 연차"),
            PayloadDocumentation.fieldWithPath("commentContent").description("댓글 내용"),
            PayloadDocumentation.fieldWithPath("likeAmount").description("댓글 좋아요 수"),
            PayloadDocumentation.fieldWithPath("liked").description("댓글 좋아요 여부"),
        )
    }



    // voteId == 1인 투표 게시글에 대한 댓글 30개를 저장합니다.
    // 댓글에 좋아요는 (30 - id) +1 만큼 추가됩니다. ex) [id: 1, likeAmount: 30], [id: 2, likeAmount: 29], ... [id: 30, likeAmount: 1]
    private fun saveDummyComments() {
        val member = memberRepository.saveAll(
            listOf(
                Member("MemberA", JobCategory.DEVELOPER, 3),
                Member("MemberB", JobCategory.DESIGNER, 5),
                Member("MemberC", JobCategory.PRODUCT_MANAGER, 4),
            )
        )

        val vote = voteRepository.save(
            Vote("VoteA", JobCategory.DEVELOPER, "ContentA", VoteType.TEXT, createdBy = member[0])
        )

        val sampleComments: MutableList<Comment> = mutableListOf()
        for (i in 1..10) {
            sampleComments.add(Comment(member[0], "Comment $i", vote))
            sampleComments.add(Comment(member[1], "Comment $i", vote))
            sampleComments.add(Comment(member[2], "Comment $i", vote))
        }

        for (i in 0 until sampleComments.size) {
            val comment = sampleComments[i]
            for (j in i + 1 downTo 1) {
                val commentLikes = CommentLikes(member[i % 3], comment)
                comment.addCommentLikes(commentLikes)
            }
        }
        commentRepository.saveAll(sampleComments)
    }
}
