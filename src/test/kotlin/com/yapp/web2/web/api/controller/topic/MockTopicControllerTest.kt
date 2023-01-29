package com.yapp.web2.web.api.controller.topic

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.yapp.web2.common.EntityFactory
import com.yapp.web2.common.TestMember
import com.yapp.web2.domain.topic.model.TopicCategory
import com.yapp.web2.domain.topic.repository.TopicRepository
import com.yapp.web2.web.api.controller.ApiControllerTest
import com.yapp.web2.web.dto.jwt.response.JwtTokens
import com.yapp.web2.web.dto.topic.request.TopicPostRequest
import com.yapp.web2.web.dto.voteoption.request.VoteOptionPostRequest
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class MockTopicControllerTest : ApiControllerTest(uri = "/api/v1/topic") {

    private val jwtTokens = JwtTokens("access-token", "refresh-token")

    @MockkBean
    lateinit var topicRepository: TopicRepository

    @Test
    @TestMember
    fun `투표게시글 등록 API 테스트`() {
        val testMemberA = EntityFactory.testMemberA()
        every { topicRepository.save(any()) }.returns(EntityFactory.testTopicA(testMemberA))

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

        val uri = "$uri"
        mockMvc.perform(
            RestDocumentationRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(topicPostRequest))
                .header("Authorization", jwtTokens.accessToken)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("SUCCESS"))
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                MockMvcRestDocumentation.document(
                    "post-topic",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("회원 AccessToken")
                    ),
                    PayloadDocumentation.requestFields(
                        *topicPostRequestFieldsSnippet()
                    ).andWithPrefix("voteOptions[].", *voteOptionPostRequestFieldsSnippet()),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.beneathPath("data").withSubsectionId("data"),
                        *topicPostResponseFieldsSnippet(),
                    )
                ),
            )
    }

    private fun topicPostRequestFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            PayloadDocumentation.fieldWithPath("title").description("투표 게시글 제목"),
            PayloadDocumentation.fieldWithPath("contents").description("투표 게시글 내용"),
            PayloadDocumentation.fieldWithPath("topicCategory").description("투표 게시글 카테고리"),
            PayloadDocumentation.subsectionWithPath("voteOptions").description("투표 선택지"),
            PayloadDocumentation.fieldWithPath("tags[]").description("태그").optional(),
        )
    }

    private fun voteOptionPostRequestFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            PayloadDocumentation.fieldWithPath("text").description("투표 선택지 텍스트"),
            PayloadDocumentation.fieldWithPath("image").type(JsonFieldType.STRING).description("투표 선택지 이미지").optional(),
            PayloadDocumentation.fieldWithPath("codeBlock").type(JsonFieldType.STRING).description("투표 선택지 코드블럭").optional(),
        )
    }

    private fun topicPostResponseFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            PayloadDocumentation.fieldWithPath("topicId").description("투표 게시글 Id"),
            PayloadDocumentation.fieldWithPath("title").description("투표 게시글 제목"),
            PayloadDocumentation.fieldWithPath("voteType").description("투표 게시글 형식"),
            PayloadDocumentation.subsectionWithPath("postMemberNickname").description("투표 게시글 작성자 닉네임"),
        )
    }
}
