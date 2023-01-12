package com.yapp.web2.domain.topic.repository

import com.yapp.web2.domain.topic.model.HashTag
import org.springframework.data.jpa.repository.JpaRepository

interface HashTagRepository : JpaRepository<HashTag, Long>
