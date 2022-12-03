package com.yapp.web2.domain.vote.repository.option

import com.yapp.web2.domain.vote.model.Vote
import com.yapp.web2.domain.vote.model.option.VoteOption
import org.springframework.data.jpa.repository.JpaRepository

interface VoteOptionRepository : JpaRepository<VoteOption, Long>
