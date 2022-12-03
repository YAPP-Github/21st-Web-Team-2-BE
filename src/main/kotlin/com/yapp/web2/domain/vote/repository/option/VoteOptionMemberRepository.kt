package com.yapp.web2.domain.vote.repository.option

import com.yapp.web2.domain.vote.model.Vote
import com.yapp.web2.domain.vote.model.option.VoteOptionMember
import org.springframework.data.jpa.repository.JpaRepository

interface VoteOptionMemberRepository : JpaRepository<VoteOptionMember, Long>
