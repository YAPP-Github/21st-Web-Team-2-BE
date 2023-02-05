package com.yapp.web2.domain.like.repository

import com.yapp.web2.domain.comment.model.Comment
import com.yapp.web2.domain.like.model.CommentLikes
import com.yapp.web2.domain.like.model.TopicLikes
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.topic.model.Topic
import org.springframework.data.jpa.repository.JpaRepository

interface CommentLikesRepository : JpaRepository<CommentLikes, Long> {
    fun findByLikedByAndComment(member: Member, topic: Comment): CommentLikes?
    fun existsByCommentAndLikedBy(comment: Comment, member: Member): Boolean
}
