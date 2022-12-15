package com.yapp.web2.domain.like.model

import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.vote.model.Vote
import jakarta.persistence.*

@Entity
class VoteLikes(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val likedBy: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val vote: Vote,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_likes_id")
    val id: Long = 0L
}
