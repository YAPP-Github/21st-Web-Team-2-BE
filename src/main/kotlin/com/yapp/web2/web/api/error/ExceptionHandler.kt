package com.yapp.web2.web.api.error

import com.yapp.web2.web.api.response.ApiResponse
import jakarta.validation.ConstraintViolationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@RestControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(BusinessException::class)
    fun handleBaseException(e: BusinessException): ResponseEntity<ApiResponse<ErrorCode>> {
        log.error(e.errorCode.message)
        return ResponseEntity.status(e.errorCode.status).body(ApiResponse.failure(e.errorCode))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<ErrorCode>> {
        val result = e.bindingResult.fieldErrors[0]
        val message = "[${result.field}](은)는 ${result.defaultMessage}. 입력된 값: [${result.rejectedValue}]"
        log.error(message)
        return ResponseEntity.status(ErrorCode.INVALID_REQUEST.status).body(ApiResponse.failure(ErrorCode.INVALID_REQUEST, message))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<ApiResponse<ErrorCode>> {
        val message = ex.constraintViolations.joinToString(", ") { "${it.propertyPath.last()}(은)는 ${it.message}" }
        log.error(message)
        return ResponseEntity.badRequest().body(ApiResponse.failure(ErrorCode.INVALID_REQUEST, message = message))
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleUnexpectedException(e: RuntimeException): ApiResponse<ErrorCode> {
        log.error(e.message)
        return ApiResponse.failure(ErrorCode.INTERNAL_SERVER_ERROR)
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(ExceptionHandler::class.java)
    }
}
