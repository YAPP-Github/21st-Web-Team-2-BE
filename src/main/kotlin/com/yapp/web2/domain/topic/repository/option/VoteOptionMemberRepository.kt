package com.yapp.web2.domain.topic.repository.option

import com.yapp.web2.domain.topic.model.option.VoteOptionMember
import org.springframework.data.jpa.repository.JpaRepository

interface VoteOptionMemberRepository : JpaRepository<VoteOptionMember, Long>
