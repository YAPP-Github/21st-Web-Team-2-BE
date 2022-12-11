package com.yapp.web2.domain.vote.model

import jakarta.persistence.*

@Entity
class HashTag(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    val vote: Vote,

    var hashTag: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hash_tag_id")
    val id: Long = 0L
}
