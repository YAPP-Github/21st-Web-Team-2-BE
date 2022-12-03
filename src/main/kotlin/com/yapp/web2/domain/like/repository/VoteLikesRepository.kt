package com.yapp.web2.domain.like.repository

import com.yapp.web2.domain.like.model.VoteLikes
import com.yapp.web2.domain.vote.model.Vote
import org.springframework.data.jpa.repository.JpaRepository

interface VoteLikesRepository : JpaRepository<VoteLikes, Long>
