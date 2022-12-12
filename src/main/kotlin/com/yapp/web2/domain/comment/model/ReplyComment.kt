package com.yapp.web2.domain.comment.model

import com.yapp.web2.common.entity.BaseEntity
import com.yapp.web2.domain.member.model.Member
import jakarta.persistence.*

@Entity
class ReplyComment constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val createdBy: Member,

    var contents: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    val comment: Comment,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    val id: Long = 0L
}
