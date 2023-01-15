package com.yapp.web2.domain.member.application

import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.web.api.error.BusinessException
import com.yapp.web2.web.api.error.ErrorCode
import com.yapp.web2.web.dto.member.request.NicknameDuplicationRequest
import com.yapp.web2.web.dto.member.response.NicknameDuplicationResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository
) {
    fun findByEmail(email: String) = memberRepository.findByEmail(email)
    fun existsByNickname(nicknameDuplicationRequest: NicknameDuplicationRequest): NicknameDuplicationResponse {
        if (!nicknameDuplicationRequest.isValid()) {
            throw BusinessException(ErrorCode.INVALID_NICKNAME)
        }
        return NicknameDuplicationResponse(memberRepository.existsByNickname(nicknameDuplicationRequest.nickname))
    }
}
