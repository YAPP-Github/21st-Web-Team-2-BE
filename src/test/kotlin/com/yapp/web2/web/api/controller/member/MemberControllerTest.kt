package com.yapp.web2.web.api.controller.member

import com.fasterxml.jackson.databind.ObjectMapper
import com.yapp.web2.web.api.controller.ApiControllerTest
import com.yapp.web2.web.dto.member.request.NicknameDuplicationRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MemberControllerTest(

) : ApiControllerTest(uri = "/api/v1") {
    @Test
    fun `닉네임 중복 확인 API`() {
        val nicknameDuplicationRequest = NicknameDuplicationRequest("leah")

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

    private fun nicknameDuplicationDataResponseFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            PayloadDocumentation.fieldWithPath("isDuplicated").description("중복되었다면 true, 중복되지 않는다면 false")
        )
    }
}
