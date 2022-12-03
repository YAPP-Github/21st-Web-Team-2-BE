package com.yapp.web2.domain.vote.model

import com.yapp.web2.common.entity.BaseEntity
import com.yapp.web2.domain.member.model.JobCategory
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.vote.model.option.VoteOption
import jakarta.persistence.*

@Entity
class Vote constructor(
    var title: String,

    @Enumerated(EnumType.STRING)
    var jobCategory: JobCategory,

    var contents: String,

    @Enumerated(EnumType.STRING)
    var voteType: VoteType,

    @OneToMany(mappedBy = "vote")
    val voteOptions: MutableList<VoteOption> = mutableListOf(),

    @OneToMany(mappedBy = "vote", cascade = [CascadeType.REMOVE])
    val hashTags: MutableList<HashTag> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val createdBy: Member,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    val id: Long? = null
}
