package com.yapp.web2.web.api.controller.image

import com.yapp.web2.common.TestMember
import com.yapp.web2.domain.image.ImageService
import com.yapp.web2.web.api.controller.ApiControllerTest
import com.yapp.web2.web.dto.jwt.response.JwtTokens
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ImageControllerTest @Autowired constructor(
) : ApiControllerTest(uri = "/api/v1/image") {
    @MockBean
    lateinit var imageService: ImageService

    @Test
    @TestMember
    fun `이미지 업로드 테스트`() {
        val awsPath = "https://test.s3.test-region.amazonaws.com/"
        val file = MockMultipartFile("file", "sampleA.png", "image/png", "Some bytes".toByteArray())

        `when`(imageService.uploadFiles(file))
            .thenReturn(awsPath + UUID.randomUUID(), awsPath + UUID.randomUUID())

        val uri = "$uri/upload"

        mockMvc.perform(
            multipart(uri)
                .file(file)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andDo(print())
            .andDo(
                MockMvcRestDocumentation.document(
                    "post-image",
                    preprocessRequest(Preprocessors.prettyPrint()),
                    preprocessResponse(Preprocessors.prettyPrint()),
                    responseFields(
                        fieldWithPath("code").description("요청 결과 상태 코드"),
                        fieldWithPath("message").description("상태 메세지"),
                        subsectionWithPath("data").description("업로드된 이미지 url"),
                    )
                ),
            )
    }

}
