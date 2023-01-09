package com.yapp.web2.domain.member.application

import com.yapp.web2.domain.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository
) {
    fun findByEmail(email: String) = memberRepository.findByEmail(email)
}
