package com.yapp.web2.domain.comment.model

import com.yapp.web2.common.entity.BaseEntity
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.vote.model.Vote
import jakarta.persistence.*

@Entity
class Comment constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val createdBy: Member,

    var contents: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val vote: Vote,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    val id: Long = 0L
}
