package com.yapp.web2.domain.vote.repository

import com.yapp.web2.domain.vote.model.HashTag
import com.yapp.web2.domain.vote.model.Vote
import org.springframework.data.jpa.repository.JpaRepository

interface HashTagRepository : JpaRepository<HashTag, Long>
