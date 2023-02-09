package com.yapp.web2.domain.image

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.yapp.web2.web.api.error.BusinessException
import com.yapp.web2.web.api.error.ErrorCode.OVER_FILE_UPLOAD_LIMIT
import com.yapp.web2.web.api.error.ErrorCode.UPLOAD_FILE_FAILURE
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.io.IOException
import java.util.*
import java.util.function.Consumer

@Service
class ImageService {

    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String? = null

    @Value("\${bucket.domain}")
    private val bucketHost: String? = null

    private val amazonS3: AmazonS3? = null

    fun uploadFiles(multipartFile: List<MultipartFile>): List<String> {
        val fileNameList: MutableList<String> = ArrayList()
        repeat(multipartFile.size) {
            Consumer { file: MultipartFile ->
                val fileName = file.originalFilename?.let { createFileName(it) }
                val objectMetadata =
                    ObjectMetadata()
                objectMetadata.contentLength = file.size
                objectMetadata.contentType = file.contentType
                try {
                    file.inputStream.use { inputStream ->
                        amazonS3!!.putObject(
                            PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                                .withCannedAcl(CannedAccessControlList.PublicRead)
                        )
                    }
                } catch (e: IOException) {
                    throw BusinessException(UPLOAD_FILE_FAILURE)
                } catch (e: MaxUploadSizeExceededException) {
                    throw BusinessException(OVER_FILE_UPLOAD_LIMIT)
                }
                fileNameList.add(bucketHost + fileName)
            }
        }
        return fileNameList
    }

    private fun createFileName(fileName: String): String {
        return UUID.randomUUID().toString() + getFileExtension(fileName)
    }

    private fun getFileExtension(fileName: String): String? {
        return try {
            fileName.substring(fileName.lastIndexOf("."))
        } catch (e: StringIndexOutOfBoundsException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일 ($fileName) 입니다.")
        }
    }
}
