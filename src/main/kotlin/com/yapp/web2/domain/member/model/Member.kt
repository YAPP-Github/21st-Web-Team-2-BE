package com.yapp.web2.domain.member.model

import com.yapp.web2.common.entity.BaseEntity
import com.yapp.web2.domain.comment.model.Comment
import com.yapp.web2.domain.like.model.VoteLikes
import com.yapp.web2.domain.vote.model.Vote
import com.yapp.web2.domain.vote.model.option.VoteOptionMember
import jakarta.persistence.*
import org.hibernate.annotations.Where

@Entity
@Where(clause = "status = \'ACTIVE\'")
@Table(indexes = [Index(name = "i_member", columnList = "email")])
class Member constructor(
    var nickname: String,

    var email: String,

    @Column(length = 20)
    var jobCategory: String,

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    val id: Long = 0L,
) : BaseEntity() {

}
