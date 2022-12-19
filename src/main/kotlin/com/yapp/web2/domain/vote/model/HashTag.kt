package com.yapp.web2.domain.vote.model

import jakarta.persistence.*

@Entity
@Table(indexes = [Index(name = "i_hash_tag", columnList = "hashTag")])
class HashTag constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val vote: Vote,

    val hashTag: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hash_tag_id")
    val id: Long = 0L
) {
}
