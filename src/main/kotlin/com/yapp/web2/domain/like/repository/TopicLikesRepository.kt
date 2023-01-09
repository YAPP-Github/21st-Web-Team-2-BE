package com.yapp.web2.domain.like.repository

import com.yapp.web2.domain.like.model.TopicLikes
import org.springframework.data.jpa.repository.JpaRepository

interface TopicLikesRepository : JpaRepository<TopicLikes, Long>
