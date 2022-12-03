package com.yapp.web2.domain.vote.model.option

import com.yapp.web2.domain.member.model.Member
import jakarta.persistence.*

@Entity
class VoteOptionMember(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val votedBy: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_option_id")
    val voteOption: VoteOption,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_option_member_id")
    private val id: Long? = null
}
