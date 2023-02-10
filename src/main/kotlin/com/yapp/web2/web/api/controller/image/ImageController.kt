package com.yapp.web2.web.api.controller.image

import com.yapp.web2.common.annotation.CurrentMember
import com.yapp.web2.common.annotation.NonMember
import com.yapp.web2.domain.image.ImageService
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.web.api.response.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RequestMapping("/api/v1/image")
@RestController
class ImageController(
    private val imageService: ImageService,
) {

    @PostMapping("/upload")
    fun uploadImage(
        @RequestPart images: MutableList<MultipartFile>
    ): ApiResponse<List<String>> {
        val uploadFiles = imageService.uploadFiles(images)
        return ApiResponse.success(uploadFiles)
    }
}
