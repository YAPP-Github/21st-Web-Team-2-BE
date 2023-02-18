package com.yapp.web2.web.api.controller.image

import com.yapp.web2.common.annotation.CurrentMember
import com.yapp.web2.common.annotation.NonMember
import com.yapp.web2.domain.image.ImageService
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.web.api.response.ApiResponse
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RequestMapping("/api/v1/image")
@RestController
class ImageController(
    private val imageService: ImageService,
) {

    @NonMember
    @PostMapping("/upload")
    fun uploadImage(
        @CurrentMember member: Member?,
        @RequestParam file: MultipartFile
    ): ApiResponse<String> {
        val uploadFiles = imageService.uploadFiles(file)
        return ApiResponse.success(uploadFiles)
    }
}
