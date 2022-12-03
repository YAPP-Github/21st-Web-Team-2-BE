package com.yapp.web2.domain.like.model

import com.yapp.web2.domain.comment.model.Comment
import com.yapp.web2.domain.member.model.Member
import jakarta.persistence.*

@Entity
class CommentLikes constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val likedBy: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    val comment: Comment,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_likes_id")
    private val id: Long? = null
}
