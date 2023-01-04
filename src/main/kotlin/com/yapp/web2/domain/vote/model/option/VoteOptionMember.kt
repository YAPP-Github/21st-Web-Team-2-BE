package com.yapp.web2.domain.vote.model.option

import com.yapp.web2.domain.member.model.Member
import jakarta.persistence.*

@Entity
class VoteOptionMember constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val votedBy: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_option_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val voteOption: VoteOption,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_option_member_id")
    val id: Long = 0L,
) {
}
