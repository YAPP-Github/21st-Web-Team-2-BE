package com.yapp.web2.domain.jwt.util

import com.yapp.web2.common.EntityFactory
import com.yapp.web2.domain.member.repository.MemberRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class JwtProviderTest @Autowired constructor(
    private val memberRepository: MemberRepository,
    private val jwtProvider: JwtProvider,
) {

    @Test
    fun `토큰 서명 시 이메일과 PK를 가져야 한다`() {
        val testMemberA = memberRepository.save(EntityFactory.testMemberA())

        val createAccessToken = jwtProvider.createAccessToken(testMemberA.id, testMemberA.email)
        val claimsJws = jwtProvider.parseToken(createAccessToken)

        assertThat(claimsJws.body["id"]).isNotNull
        assertThat(claimsJws.body["email"]).isNotNull
    }
}


