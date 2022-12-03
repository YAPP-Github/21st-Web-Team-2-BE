package com.yapp.web2.domain.member.model

import com.yapp.web2.common.entity.BaseEntity
import com.yapp.web2.domain.comment.model.Comment
import com.yapp.web2.domain.like.model.VoteLikes
import com.yapp.web2.domain.vote.model.Vote
import com.yapp.web2.domain.vote.model.option.VoteOption
import com.yapp.web2.domain.vote.model.option.VoteOptionMember
import jakarta.persistence.*

@Entity
class Member constructor(
    var nickname: String,

    @Enumerated(EnumType.STRING)
    var jobCategory: JobCategory,

    var workingYears: Int,

    var profileImageFilename: String? = null,

    @OneToMany(mappedBy = "createdBy")
    val comments: MutableList<Comment> = mutableListOf(),

    @OneToMany(mappedBy = "createdBy")
    val votes: MutableList<Vote> = mutableListOf(),

    @OneToMany(mappedBy = "likedBy")
    val voteLikes: MutableList<VoteLikes> = mutableListOf(),

    @OneToMany(mappedBy = "votedBy")
    val voteOptions: MutableList<VoteOptionMember> = mutableListOf(),

    ) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    val id: Long? = null
}