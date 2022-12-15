package com.yapp.web2.domain.like.model

import com.yapp.web2.domain.comment.model.Comment
import com.yapp.web2.domain.member.model.Member
import jakarta.persistence.*

@Entity
class CommentLikes constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val likedBy: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val comment: Comment,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_likes_id")
    val id: Long = 0L
}
