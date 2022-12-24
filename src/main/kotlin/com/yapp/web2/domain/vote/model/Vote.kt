package com.yapp.web2.domain.vote.model

import com.yapp.web2.common.entity.BaseEntity
import com.yapp.web2.domain.comment.model.Comment
import com.yapp.web2.domain.member.model.JobCategory
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.vote.model.option.VoteOption
import jakarta.persistence.*
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Where

@Entity
@Where(clause = "status = \'ACTIVE\'")
class Vote constructor(
    var title: String,

    @Enumerated(EnumType.STRING)
    var jobCategory: JobCategory,

    @Column(length = 1000)
    var contents: String,

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    var voteType: VoteType,

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "vote")
    val voteOptions: MutableSet<VoteOption> = mutableSetOf(),

    @OneToMany(mappedBy = "vote")
    val hashTags: MutableList<HashTag> = mutableListOf(),

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "vote")
    val comments: MutableSet<Comment> = mutableSetOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val createdBy: Member,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    val id: Long = 0L,
) : BaseEntity() {
}
