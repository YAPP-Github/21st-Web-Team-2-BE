package com.yapp.web2.domain.jwt.application

import com.yapp.web2.common.EntityFactory
import com.yapp.web2.domain.jwt.util.JwtProvider
import com.yapp.web2.domain.jwt.util.JwtUtil
import com.yapp.web2.domain.member.application.MemberService
import com.yapp.web2.domain.member.repository.MemberRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class JwtServiceTest {
    @InjectMockKs
    lateinit var jwtService: JwtService

    @MockK
    lateinit var jwtProvider: JwtProvider

    @MockK
    lateinit var jwtUtil: JwtUtil

    @MockK
    lateinit var memberService: MemberService

    @MockK
    lateinit var memberRepository: MemberRepository

    private val testMemberA = EntityFactory.testMemberA()

    @Test
    fun `토큰 발급`() {
        every { memberService.findByEmail(testMemberA.email) }.returns(testMemberA)
        every { jwtProvider.createAccessToken(testMemberA.id, testMemberA.email) }.returns("access-token")
        every { jwtProvider.createRefreshToken() }.returns("refresh-token")

        val jwtTokens = jwtService.issue(testMemberA.email)

        assertThat(jwtTokens.accessToken).isNotNull
        assertThat(jwtTokens.refreshToken).isNotNull
    }
}
