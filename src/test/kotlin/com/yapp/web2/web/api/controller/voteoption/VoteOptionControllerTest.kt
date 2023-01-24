package com.yapp.web2.web.api.controller.voteoption

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.clear
import com.yapp.web2.common.EntityFactory
import com.yapp.web2.domain.jwt.util.JwtProvider
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.topic.application.option.VoteOptionService
import com.yapp.web2.domain.topic.repository.option.VoteOptionMemberRepository
import com.yapp.web2.web.api.controller.ApiControllerTest
import com.yapp.web2.web.dto.voteoption.request.VotePostRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.*
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@TestInstance(TestInstance.Lifecycle.PER_CLASS)

internal class VoteOptionControllerTest @Autowired constructor(
    val jwtProvider: JwtProvider,
    val memberRepository: MemberRepository,
) : ApiControllerTest(uri = "/api/v1/vote/option") {

    @MockBean
    lateinit var voteOptionService: VoteOptionService

    @Test
    fun `투표 등록 테스트`() {
        val testMemberA = EntityFactory.testMemberA()
        memberRepository.save(testMemberA)
        val topicA = EntityFactory.testTopicA(testMemberA)

        val votePostRequest = VotePostRequest(topicA.id, topicA.voteOptions[0].id)
        doNothing().`when`(voteOptionService).vote(testMemberA, votePostRequest)

        val accessToken = jwtProvider.createAccessToken(testMemberA.id, testMemberA.email)
        val uri = "$uri"
        mockMvc.perform(
            RestDocumentationRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(votePostRequest))
                .header("Authorization", accessToken)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("SUCCESS"))
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                document(
                    "post-vote",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("회원 AccessToken")
                    ),
                    requestFields(*topicPostRequestFieldsSnippet()),
                    responseFields(
                        fieldWithPath("code").description("요청 결과 상태 코드"),
                        fieldWithPath("message").description("상태 메세지"),
                    )
                ),
            )
    }

    private fun topicPostRequestFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("topicId").description("투표 게시글 Id"),
            fieldWithPath("voteOptionId").description("선택한 투표 선택지 Id"),
        )
    }
}
