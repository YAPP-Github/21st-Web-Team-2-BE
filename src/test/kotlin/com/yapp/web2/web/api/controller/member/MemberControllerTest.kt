package com.yapp.web2.web.api.controller.member

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.yapp.web2.common.EntityFactory
import com.yapp.web2.common.TestMember
import com.yapp.web2.domain.member.application.MemberService
import com.yapp.web2.web.api.controller.ApiControllerTest
import com.yapp.web2.web.dto.member.request.NicknameDuplicationRequest
import com.yapp.web2.web.dto.member.response.MemberResponse
import com.yapp.web2.web.dto.member.response.NicknameDuplicationResponse
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MemberControllerTest : ApiControllerTest(uri = "/api/v1") {

    @MockkBean
    lateinit var memberService: MemberService

    @Test
    fun `닉네임 중복 확인 API`() {
        val nicknameDuplicationRequest = NicknameDuplicationRequest("leah")
        every { memberService.existsByNickname(any()) }.returns(NicknameDuplicationResponse(false))

        val uri = "$uri/nickname-duplication"
        mockMvc.perform(
            RestDocumentationRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(nicknameDuplicationRequest))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("SUCCESS"))
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                MockMvcRestDocumentation.document(
                    "post-nickname-duplication", // docs directory name
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.beneathPath("data").withSubsectionId("data"),
                        *nicknameDuplicationDataResponseFieldsSnippet(),
                    )
                )
            )
    }

    @Test
    @TestMember
    fun `유저 정보 조회 API`() {
        val memberId = 1L
        val uri = "$uri/member/{memberId}"

        every { memberService.getMember(any()) }.returns(MemberResponse.of(EntityFactory.testMemberA()))

        mockMvc.perform(
            RestDocumentationRequestBuilders.get(uri, memberId)
                .header("Authorization", "accesstoken")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("SUCCESS"))
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                MockMvcRestDocumentation.document(
                    "get-member", // docs directory name
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    HeaderDocumentation.requestHeaders(
                        HeaderDocumentation.headerWithName("Authorization").description("회원 AccessToken")
                    ),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.beneathPath("data").withSubsectionId("data"),
                        *memberDataResponseFieldsSnippet(),
                    )
                )
            )
    }

    private fun memberDataResponseFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            PayloadDocumentation.fieldWithPath("memberId").description("작성자 Id"),
            PayloadDocumentation.fieldWithPath("nickname").description("작성자 닉네임"),
            PayloadDocumentation.fieldWithPath("profileImage").type(JsonFieldType.STRING).description("작성자 프로필 이미지").optional(),
            PayloadDocumentation.fieldWithPath("jobCategory").description("작성자 직군"),
            PayloadDocumentation.fieldWithPath("workingYears").description("작성자 연차"),
        )
    }


    private fun nicknameDuplicationDataResponseFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            PayloadDocumentation.fieldWithPath("isDuplicated").description("중복되었다면 true, 중복되지 않는다면 false")
        )
    }
}
