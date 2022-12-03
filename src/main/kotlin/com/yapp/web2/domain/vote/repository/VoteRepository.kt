package com.yapp.web2.domain.vote.repository

import com.yapp.web2.domain.vote.model.Vote
import org.springframework.data.jpa.repository.JpaRepository

interface VoteRepository : JpaRepository<Vote, Long>
