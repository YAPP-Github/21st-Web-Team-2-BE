package com.yapp.web2.domain.member.application

import com.yapp.web2.domain.jwt.application.oauth.AuthService
import com.yapp.web2.domain.jwt.application.JwtService
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.web.api.dto.jwt.response.JwtTokens
import com.yapp.web2.web.api.dto.member.response.SignIn
import com.yapp.web2.web.api.dto.sign.request.SignUpDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SignService(
    private val memberRepository: MemberRepository,
    private val authService: AuthService,
    private val jwtService: JwtService
) {
    fun signIn(token: String): SignIn {
        val email = authService.getUserEmail(token)
        val isExist = memberRepository.existsByEmail(email)

        if (!isExist) {
            return SignIn(false, JwtTokens())
        }

        return SignIn(true, jwtService.issue(email))
    }

    @Transactional
    fun signup(token: String, signUpDto: SignUpDto): JwtTokens {
        val email = authService.getUserEmail(token)
        join(email, signUpDto)
        return jwtService.issue(email)
    }

    private fun join(email: String, signUpDto: SignUpDto) {
        memberRepository.save(
            Member(
                email = email,
                nickname = signUpDto.nickname,
                jobCategory = signUpDto.jobCategory,
                workingYears = signUpDto.workingYears
            )
        )
    }
}
