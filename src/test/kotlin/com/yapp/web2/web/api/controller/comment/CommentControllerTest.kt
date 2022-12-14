package com.yapp.web2.web.api.controller.comment

import com.yapp.web2.common.EntityFactory
import com.yapp.web2.domain.comment.model.Comment
import com.yapp.web2.domain.comment.respository.CommentRepository
import com.yapp.web2.domain.like.model.CommentLikes
import com.yapp.web2.domain.member.model.JobCategory
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.topic.model.Topic
import com.yapp.web2.domain.topic.model.VoteType
import com.yapp.web2.domain.topic.repository.TopicRepository
import com.yapp.web2.web.api.controller.ApiControllerTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CommentControllerTest @Autowired constructor(
    val topicRepository: TopicRepository,
    val memberRepository: MemberRepository,
    val commentRepository: CommentRepository,
) : ApiControllerTest(uri = "/api/v1/comment") {

    lateinit var topic: Topic
    @BeforeAll
    fun saveTestData() {
        topic = saveDummyComments()
    }

    @Test
    fun `getCommentsNoOffsetTest`() {
        val findTopicId = topic.id
        val uri = "$uri/{topicId}/latest"
        mockMvc.perform(
            RestDocumentationRequestBuilders.get(uri, findTopicId)
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
                        RequestDocumentation.parameterWithName("topicId").description("?????? ????????? Id")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.beneathPath("data").withSubsectionId("data"),
                        *commentDataResponseFieldSnippet(),
                        *memberPreviewDataResponseFieldsSnippet(),
                    )
                ),
            )
    }

    private fun commentDataResponseFieldSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            PayloadDocumentation.fieldWithPath("commentId").description("?????? Id"),
            PayloadDocumentation.fieldWithPath("commentContent").description("?????? ??????"),
            PayloadDocumentation.fieldWithPath("likeAmount").description("?????? ????????? ???"),
            PayloadDocumentation.fieldWithPath("liked").description("?????? ????????? ??????"),
        )
    }


    private fun memberPreviewDataResponseFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            PayloadDocumentation.fieldWithPath("member.id").description("????????? Id"),
            PayloadDocumentation.fieldWithPath("member.name").description("????????? ?????????"),
            PayloadDocumentation.fieldWithPath("member.profileImage").type(JsonFieldType.STRING).description("????????? ????????? ?????????").optional(),
            PayloadDocumentation.fieldWithPath("member.jobCategory").description("????????? ??????"),
            PayloadDocumentation.fieldWithPath("member.workingYears").description("????????? ??????"),
        )
    }



    // topicId == 1??? ?????? ???????????? ?????? ?????? 30?????? ???????????????.
    // ????????? ???????????? (30 - id) +1 ?????? ???????????????. ex) [id: 1, likeAmount: 30], [id: 2, likeAmount: 29], ... [id: 30, likeAmount: 1]
    private fun saveDummyComments(): Topic {
        val member = memberRepository.saveAll(
            listOf(
                EntityFactory.testMemberA(),
                EntityFactory.testMemberB(),
                EntityFactory.testMemberC(),
            )
        )

        val topic = topicRepository.save(
            Topic("VoteA", JobCategory.DEVELOPER, "ContentA", VoteType.TEXT, createdBy = member[0])
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
