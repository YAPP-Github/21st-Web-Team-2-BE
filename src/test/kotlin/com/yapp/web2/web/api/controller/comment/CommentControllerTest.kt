package com.yapp.web2.web.api.controller.comment

import com.fasterxml.jackson.databind.ObjectMapper
import com.yapp.web2.common.EntityFactory
import com.yapp.web2.common.TestMember
import com.yapp.web2.domain.comment.model.Comment
import com.yapp.web2.domain.comment.respository.CommentRepository
import com.yapp.web2.domain.like.model.CommentLikes
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.topic.model.Topic
import com.yapp.web2.domain.topic.model.TopicCategory
import com.yapp.web2.domain.topic.model.VoteType
import com.yapp.web2.domain.topic.repository.TopicRepository
import com.yapp.web2.web.api.controller.ApiControllerTest
import com.yapp.web2.web.dto.comment.request.CommentLikePostRequest
import com.yapp.web2.web.dto.comment.request.CommentPostRequest
import com.yapp.web2.web.dto.jwt.response.JwtTokens
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CommentControllerTest @Autowired constructor(
    val topicRepository: TopicRepository,
    val commentRepository: CommentRepository,
    val memberRepository: MemberRepository,
) : ApiControllerTest(uri = "/api/v1/comment") {

    lateinit var topic: Topic
    private val jwtTokens = JwtTokens("access-token", "refresh-token")

    @BeforeAll
    fun saveTestData() {
        topic = saveDummyComments()
    }

    @Test
    fun `댓글 조회 테스트`() {
        val findTopicId = topic.id
        val uri = "$uri/{topicId}/latest"
        mockMvc.perform(
            get(uri, findTopicId)
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
                        RequestDocumentation.parameterWithName("topicId").description("투표 게시글 Id")
                    ),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        *commentDataResponseFieldSnippet(),
                        *memberPreviewDataResponseFieldsSnippet(),
                    )
                ),
            )
    }

    @Test
    @TestMember
    fun `댓글 등록 테스트`() {
        val commentPostRequest = CommentPostRequest(
            topic.id,
            "Comment content"
        )

        val uri = "$uri"
        mockMvc.perform(
            post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(commentPostRequest))
                .header("Authorization", jwtTokens.accessToken)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("SUCCESS"))
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                MockMvcRestDocumentation.document(
                    "post-comment",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("회원 AccessToken")
                    ),
                    requestFields(*commentPostRequestFieldSnippet()),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        *commentPostResponseFieldSnippet(),
                    )
                ),
            )
    }

    @Test
    @TestMember
    fun `댓글 좋아요 테스트`() {
        val commentId = commentRepository.findAll()[0].id
        val commentLikesRequest = CommentLikePostRequest(
            commentId
        )

        val uri = "$uri/likes"
        mockMvc.perform(
            post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(commentLikesRequest))
                .header("Authorization", jwtTokens.accessToken)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("SUCCESS"))
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                MockMvcRestDocumentation.document(
                    "post-comment-like",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("회원 AccessToken")
                    ),
                    requestFields(*commentLikesPostRequestFieldSnippet()),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        *commentLikesPostResponseFieldSnippet(),
                    )
                ),
            )
    }

    private fun commentDataResponseFieldSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("commentId").description("댓글 Id"),
            fieldWithPath("contents").description("댓글 내용"),
            fieldWithPath("likeAmount").description("댓글 좋아요 수"),
            fieldWithPath("liked").description("댓글 좋아요 여부"),
        )
    }

    private fun commentPostRequestFieldSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("topicId").description("투표 게시글 Id"),
            fieldWithPath("contents").description("댓글 내용"),
        )
    }

    private fun commentPostResponseFieldSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("commentId").description("추가된 댓글 Id"),
            fieldWithPath("contents").description("댓글 내용"),
        )
    }


    private fun memberPreviewDataResponseFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("member.memberId").description("작성자 Id"),
            fieldWithPath("member.nickname").description("작성자 닉네임"),
            fieldWithPath("member.profileImage").type(JsonFieldType.STRING).description("작성자 프로필 이미지").optional(),
            fieldWithPath("member.jobCategory").description("작성자 직군"),
            fieldWithPath("member.workingYears").description("작성자 연차"),
        )
    }

    private fun commentLikesPostRequestFieldSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("commentId").description("좋아요 한 댓글 Id"),
        )
    }

    private fun commentLikesPostResponseFieldSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("commentId").description("좋아요 한 댓글 Id"),
            fieldWithPath("liked").description("좋아요 여부"),
        )
    }


    // topicId == 1인 투표 게시글에 대한 댓글 30개를 저장합니다.
    // 댓글에 좋아요는 (30 - id) +1 만큼 추가됩니다. ex) [id: 1, likeAmount: 30], [id: 2, likeAmount: 29], ... [id: 30, likeAmount: 1]
    private fun saveDummyComments(): Topic {
        val member = memberRepository.saveAll(
            listOf(
                EntityFactory.testMemberA(),
                EntityFactory.testMemberB(),
                EntityFactory.testMemberC(),
            )
        )

        val topic = topicRepository.save(
            Topic("VoteA", TopicCategory.DEVELOPER, "ContentA", VoteType.TEXT, createdBy = member[0])
        )

        val sampleComments: MutableList<Comment> = mutableListOf()
        for (i in 1..10) {
            sampleComments.add(Comment(member[0], "Comment $i", topic))
            sampleComments.add(Comment(member[1], "Comment $i", topic))
            sampleComments.add(Comment(member[2], "Comment $i", topic))
        }

        for (i in 0 until sampleComments.size) {
            val comment = sampleComments[i]
            for (j in i + 1 downTo 1) {
                val commentLikes = CommentLikes(member[i % 3], comment)
                comment.addCommentLikes(commentLikes)
            }
        }
        commentRepository.saveAll(sampleComments)

        return topic
    }
}
