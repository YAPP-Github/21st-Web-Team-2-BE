package com.yapp.web2.domain.member.repository

import com.yapp.web2.domain.member.model.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long>
