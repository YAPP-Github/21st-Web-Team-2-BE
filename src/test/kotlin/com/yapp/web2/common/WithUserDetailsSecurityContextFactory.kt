package com.yapp.web2.common

import com.yapp.web2.domain.jwt.user.UserDetailsImpl
import com.yapp.web2.domain.member.repository.MemberRepository
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.test.context.support.WithSecurityContextFactory

internal class WithUserDetailsSecurityContextFactory(
    private val memberRepository: MemberRepository,
) : WithSecurityContextFactory<TestMember> {
    override fun createSecurityContext(annotation: TestMember?): SecurityContext {
        val member = EntityFactory.testMemberA()
        memberRepository.save(member)
        val principal: UserDetails = UserDetailsImpl(member)
        val authentication: Authentication = UsernamePasswordAuthenticationToken(principal, null, principal.authorities)
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authentication
        return context
    }
}
