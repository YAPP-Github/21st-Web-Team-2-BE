package com.yapp.web2.domain.topic.application.vo

data class LatestTopicSliceVo(
    val topics: MutableList<TopicPreviewVo>,
    val hasNext: Boolean,
) {
}
