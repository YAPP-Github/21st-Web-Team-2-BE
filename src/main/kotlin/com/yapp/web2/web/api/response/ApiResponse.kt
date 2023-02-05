package com.yapp.web2.web.api.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.yapp.web2.common.annotation.Generated
import com.yapp.web2.web.api.error.ErrorCode
import org.springframework.data.domain.Slice

data class ApiResponse<T>(
    val code: String,

    val message: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val data: T?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val pageSize: Int? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val pageNumber: Int? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val hasNext: Boolean? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val offsetId: Long? = null,
) {
    @Generated
    companion object {
        fun success() = ApiResponse(
            code = "SUCCESS",
            message = "성공",
            data = null,
        )

        fun <T> success(data: T) = ApiResponse(
            code = "SUCCESS",
            message = "성공",
            data = data,
        )

        fun <T : OffsetIdSupport> success(slice: Slice<T>) = ApiResponse(
            code = "SUCCESS",
            message = "성공",
            data = slice.content,
            hasNext = slice.hasNext(),
            offsetId = if (!slice.hasContent()) null else slice.content.last().offsetId,
        )

        fun <T : OffsetIdSupport> successWithPage(slice: Slice<T>) = ApiResponse(
            code = "SUCCESS",
            message = "성공",
            data = slice.content,
            hasNext = slice.hasNext(),
            pageNumber = slice.number,
            offsetId = null,
        )

        fun failure(errorCode: ErrorCode) = ApiResponse<ErrorCode>(
            code = errorCode.code,
            message = errorCode.message,
            data = null,
        )

        fun failure(errorCode: ErrorCode, message: String) = ApiResponse<ErrorCode>(
            code = errorCode.code,
            message = message,
            data = null
        )
    }
}
