package com.yapp.web2.web.api.controller

import org.junit.jupiter.api.Test
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

internal class VoteControllerTest : ApiControllerTest(uri = "/api/v1/vote") {

    @Test
    fun commonResponseDocsTest() {
        val uri = "$uri/popular"
        mockMvc.perform(
            RestDocumentationRequestBuilders.get(uri)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andDo(print())
            .andDo(

                document(
                    "common", // docs directory name
                    responseFields(
                        subsectionWithPath("data").description("데이터"),
                        fieldWithPath("code").description("요청 결과 상태 코드"),
                        fieldWithPath("message").description("상태 메세지"),
                    )
                )
            )
    }

    @Test
    fun commonSliceResponseDocsTest() {
        val uri = "$uri/latest"
        mockMvc.perform(
            RestDocumentationRequestBuilders.get(uri)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andDo(print())
            .andDo(

                document(
                    "common-slice", // docs directory name
                    responseFields(
                        subsectionWithPath("data").description("데이터"),
                        fieldWithPath("code").description("요청 결과 상태 코드"),
                        fieldWithPath("message").description("상태 메세지"),
                        fieldWithPath("hasNext").description("다음 페이지 존재 여부"),
                        fieldWithPath("offsetId").type(JsonFieldType.NUMBER).description("다음 페이지 조회에 사용되는 offsetId").optional(),
                    )
                )
            )
    }
}
