package com.yapp.web2.domain.vote.application.vo

import com.yapp.web2.domain.vote.model.Vote

data class VoteDetailVo constructor(
    val vote: Vote,
    val commentCount: Long,
    val voteAmount: Long,
    val likedAmount: Long,
)
