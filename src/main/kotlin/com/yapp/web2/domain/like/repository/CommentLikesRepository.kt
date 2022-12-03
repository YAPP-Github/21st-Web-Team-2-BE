package com.yapp.web2.domain.like.repository

import com.yapp.web2.domain.like.model.CommentLikes
import org.springframework.data.jpa.repository.JpaRepository

interface CommentLikesRepository : JpaRepository<CommentLikes, Long>
