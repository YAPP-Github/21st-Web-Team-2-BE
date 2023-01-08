package com.yapp.web2.domain.jwt.user

import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.web.api.error.BusinessException
import com.yapp.web2.web.api.error.ErrorCode
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val memberRepository: MemberRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return UserDetailsImpl(memberRepository.findByEmail(username)
            ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND))
    }
}
