package com.yapp.web2.domain.comment.respository

import com.yapp.web2.domain.comment.model.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long>
