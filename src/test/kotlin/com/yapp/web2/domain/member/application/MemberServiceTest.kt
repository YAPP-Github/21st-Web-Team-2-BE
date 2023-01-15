package com.yapp.web2.domain.member.application

import com.yapp.web2.common.EntityFactory
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.web.api.error.BusinessException
import com.yapp.web2.web.dto.member.request.NicknameDuplicationRequest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class MemberServiceTest {
    @InjectMockKs
    lateinit var memberService: MemberService

    @MockK
    lateinit var memberRepository: MemberRepository

    @Test
    fun `닉네임 길이가 20자 넘어간다면 예외`() {
        Assertions.assertThatThrownBy { memberService.existsByNickname(NicknameDuplicationRequest("20자이상20자이상20자이상20자이상20자이상")) }
            .isInstanceOf(BusinessException::class.java)
    }

    @Test
    fun `존재하는 닉네임이라면 true 반환`() {
        every { memberRepository.existsByNickname("닉네임") }.returns(true)
        Assertions.assertThat(memberService.existsByNickname(NicknameDuplicationRequest("닉네임")).isDuplicated).isTrue
    }

    @Test
    fun `존재하지 않는 닉네임이라면 false 반환`() {
        every { memberRepository.existsByNickname("닉네임") }.returns(false)
        Assertions.assertThat(memberService.existsByNickname(NicknameDuplicationRequest("닉네임")).isDuplicated).isFalse
    }

    @Test
    fun `존재하는 이메일이라면 멤버 반환`() {
        every { memberRepository.findByEmail("MemberA@test.com") }.returns(EntityFactory.testMemberA())
        Assertions.assertThat(memberService.findByEmail("MemberA@test.com")).isNotNull
    }

    @Test
    fun `존재하지 않는 이메일이라면 false 반환`() {
        every { memberRepository.findByEmail("email@test.com") }.returns(null)
        Assertions.assertThat(memberService.findByEmail("email@test.com")).isNull()
    }
}
