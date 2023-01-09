package com.yapp.web2.domain.topic.application.vo

import com.yapp.web2.domain.topic.model.Topic

data class TopicPreviewVo constructor(
    val topic: Topic,
    val commentCount: Long,
    val voteAmount: Long,
)
