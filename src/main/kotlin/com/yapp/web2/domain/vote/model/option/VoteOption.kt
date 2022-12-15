package com.yapp.web2.domain.vote.model.option

import com.yapp.web2.common.entity.BaseEntity
import com.yapp.web2.domain.vote.model.Vote
import jakarta.persistence.*

@Entity
class VoteOption(
    val text: String?,

    val voteOptionImageFilename: String?,

    val codeBlock: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val vote: Vote,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_option_id")
    val id: Long? = null
}
