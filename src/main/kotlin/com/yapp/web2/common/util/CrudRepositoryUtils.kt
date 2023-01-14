package com.yapp.web2.common.util

import com.yapp.web2.web.api.error.BusinessException
import com.yapp.web2.web.api.error.ErrorCode
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull

fun <T, ID> CrudRepository<T, ID>.findByIdOrThrow(id: ID): T {
    return this.findByIdOrNull(id) ?: throwNoSuchEntity()
}

private fun throwNoSuchEntity(): Nothing {
    throw BusinessException(ErrorCode.NOT_FOUND_DATA)
}

