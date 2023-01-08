package com.yapp.web2.domain.member.application

import com.yapp.web2.common.EntityFactory
import com.yapp.web2.domain.jwt.application.JwtService
import com.yapp.web2.domain.jwt.application.oauth.AuthService
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.web.api.dto.jwt.response.JwtTokens
import com.yapp.web2.web.api.dto.sign.request.SignUpDto
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class SignServiceTest {
    @InjectMockKs
    lateinit var signService: SignService

    @MockK
    lateinit var authService: AuthService

    @MockK
    lateinit var memberRepository: MemberRepository

    @MockK
    lateinit var memberService: MemberService

    @MockK
    lateinit var jwtService: JwtService

    @Test
    fun `회원가입 성공`() {
        every { authService.getUserEmail(any()) }.returns("MemberA@test.com")
        every { memberRepository.save(any()) } returns EntityFactory.testMemberA()
        every { jwtService.issue("MemberA@test.com") }.returns(JwtTokens("access-token", "refresh-token"))

        val signUpRequestDto = SignUpDto("MemberA", "developer", 3)
        val signUpResponseDto = signService.signup("token", signUpRequestDto)

        assertAll(
            { assertThat(signUpResponseDto.accessToken).isNotNull },
            { assertThat(signUpResponseDto.refreshToken).isNotNull },
        )
    }

    @Test
    fun `기가입자라면 로그인 성공`() {
        every { authService.getUserEmail(any()) }.returns("MemberA@test.com")
        every { memberRepository.existsByEmail("MemberA@test.com") }.returns(true)
        every { memberService.findByEmail("MemberA@test.com") }.returns(EntityFactory.testMemberA())
        every { jwtService.issue("MemberA@test.com") }.returns(JwtTokens("access-token", "refresh-token"))

        val signInResponseDto = signService.signIn("token")

        assertAll(
            { assertThat(signInResponseDto.jwtTokens.accessToken).isNotNull },
            { assertThat(signInResponseDto.jwtTokens.refreshToken).isNotNull },
            { assertThat(signInResponseDto.isMember).isTrue },
        )
    }

    @Test
    fun `기가입자가 아니라면 로그인 실패`() {
        every { authService.getUserEmail(any()) }.returns("MemberA@test.com")
        every { memberRepository.existsByEmail("MemberA@test.com") }.returns(false)
        every { memberService.findByEmail("MemberA@test.com") }.returns(EntityFactory.testMemberA())
        every { jwtService.issue("MemberA@test.com") }.returns(JwtTokens())

        val signInResponseDto = signService.signIn("token")

        assertAll(
            { assertThat(signInResponseDto.jwtTokens.accessToken).isNull() },
            { assertThat(signInResponseDto.jwtTokens.refreshToken).isNull() },
            { assertThat(signInResponseDto.isMember).isFalse() },
        )
    }
}
