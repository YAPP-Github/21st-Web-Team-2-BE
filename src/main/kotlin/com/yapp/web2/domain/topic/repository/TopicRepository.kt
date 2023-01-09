package com.yapp.web2.domain.topic.repository

import com.yapp.web2.domain.topic.model.Topic
import org.springframework.data.jpa.repository.JpaRepository

interface TopicRepository : JpaRepository<Topic, Long>
