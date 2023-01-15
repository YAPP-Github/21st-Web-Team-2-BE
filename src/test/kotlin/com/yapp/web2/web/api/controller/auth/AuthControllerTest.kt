package com.yapp.web2.web.api.controller.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.yapp.web2.domain.jwt.application.AuthService
import com.yapp.web2.web.api.controller.ApiControllerTest
import com.yapp.web2.web.dto.jwt.response.JwtTokens
import com.yapp.web2.web.dto.auth.response.SignInResponse
import com.yapp.web2.web.dto.auth.request.SignUpRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.`when`
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class AuthControllerTest : ApiControllerTest(uri = "/api/v1/auth") {
    @MockBean
    lateinit var authService: AuthService
    private val jwtTokens = JwtTokens("access-token", "refresh-token")

    @Test
    fun `회원가입`() {
        val signUpRequest = SignUpRequest("leah", "developer", 1)
        `when`(authService.signup("token", signUpRequest)).thenReturn(jwtTokens)

        val uri = "$uri/signup"
        mockMvc.perform(
            RestDocumentationRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(signUpRequest))
                .header("auth-token", "token")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("SUCCESS"))
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                MockMvcRestDocumentation.document(
                    "signup",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.beneathPath("data").withSubsectionId("data"),
                        *jwtTokensPreviewDataResponseFieldsSnippet(),
                    )
                )
            )
    }

    @Test
    fun `기가입자가 아닌 경우 로그인`() {
        `when`(authService.signIn("code")).thenReturn(SignInResponse(isMember = false, JwtTokens()))

        val uri = "$uri/signin"
        mockMvc.perform(
            RestDocumentationRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("auth-code", "code")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("SUCCESS"))
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                MockMvcRestDocumentation.document(
                    "signin-not-member",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.beneathPath("data").withSubsectionId("data"),
                        *signInNotMemberPreviewDataResponseFieldsSnippet(),
                    ).andWithPrefix("jwtTokens[].", *jwtTokensPreviewDataResponseFieldsSnippet())
                )
            )
    }

    @Test
    fun `기가입자인 경우 로그인`() {
        `when`(authService.signIn("code")).thenReturn(SignInResponse(isMember = true, jwtTokens))

        val uri = "$uri/signin"
        mockMvc.perform(
            RestDocumentationRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("auth-code", "code")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("SUCCESS"))
            .andDo(MockMvcResultHandlers.print())
            .andDo(
                MockMvcRestDocumentation.document(
                    "signin-member",
                    Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                    Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                    PayloadDocumentation.responseFields(
                        PayloadDocumentation.beneathPath("data").withSubsectionId("data"),
                        *signInNotMemberPreviewDataResponseFieldsSnippet(),
                    ).andWithPrefix("jwtTokens[].", *jwtTokensPreviewDataResponseFieldsSnippet())
                )
            )
    }

    private fun jwtTokensPreviewDataResponseFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            PayloadDocumentation.fieldWithPath("accessToken").description("엑세스 토큰").optional().type(JsonFieldType.STRING),
            PayloadDocumentation.fieldWithPath("refreshToken").description("엑세스 토큰을 재발급 받기 위한 리프레쉬 토큰").optional().type(JsonFieldType.STRING),
        )
    }

    private fun signInNotMemberPreviewDataResponseFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            PayloadDocumentation.fieldWithPath("isMember").description("이미 가입을 했다면 true, 하지 않았다면 false"),
            PayloadDocumentation.subsectionWithPath("jwtTokens").description("엑세스, 리프레쉬 토큰"),
        )
    }
}
