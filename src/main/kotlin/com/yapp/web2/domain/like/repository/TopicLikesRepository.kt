package com.yapp.web2.domain.like.repository

import com.yapp.web2.domain.like.model.TopicLikes
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.topic.model.Topic
import org.springframework.data.jpa.repository.JpaRepository

interface TopicLikesRepository : JpaRepository<TopicLikes, Long> {
    fun findByLikedByAndTopic(member: Member, topic: Topic): TopicLikes?
    fun existsByTopicAndLikedBy(topic: Topic, member: Member): Boolean
}
