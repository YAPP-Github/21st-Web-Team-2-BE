package com.yapp.web2.web.api.controller.image

import com.yapp.web2.common.TestMember
import com.yapp.web2.domain.image.ImageService
import com.yapp.web2.domain.jwt.util.JwtProvider
import com.yapp.web2.web.api.controller.ApiControllerTest
import com.yapp.web2.web.dto.jwt.response.JwtTokens
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ImageControllerTest @Autowired constructor(
) : ApiControllerTest("/api/v1/image") {
    @MockBean
    lateinit var imageService: ImageService
    private val jwtTokens = JwtTokens("access-token", "refresh-token")

    @Test
    @TestMember
    fun `이미지 업로드 테스트`() {
        val awsPath = "https://test.s3.test-region.amazonaws.com/"
        val uploadFiles = listOf(
            MockMultipartFile("images", "sampleA.png", "image/png", "Some bytes".toByteArray()),
            MockMultipartFile("images", "sampleA.png", "image/png", "Some bytes".toByteArray())
        )

        `when`(imageService.uploadFiles(uploadFiles))
            .thenReturn(listOf(awsPath + UUID.randomUUID(), awsPath + UUID.randomUUID()))

        mockMvc.perform(
            multipart("$uri/upload")
                .file(uploadFiles[0])
                .file(uploadFiles[1])
                .header("Authorization", jwtTokens.accessToken)
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andDo(print())
        //TODO: rest docs
    }

}
