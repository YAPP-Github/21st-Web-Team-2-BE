package com.yapp.web2.domain.comment.respository

import com.yapp.web2.domain.comment.model.ReplyComment
import org.springframework.data.jpa.repository.JpaRepository

interface ReplyCommentRepository : JpaRepository<ReplyComment, Long>
