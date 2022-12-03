package com.yapp.web2.domain.comment.respository

import com.yapp.web2.domain.comment.model.Comment
import com.yapp.web2.domain.vote.model.Vote
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long>
