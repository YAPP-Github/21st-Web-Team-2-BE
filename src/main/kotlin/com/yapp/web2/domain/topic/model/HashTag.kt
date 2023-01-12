package com.yapp.web2.domain.topic.model

import jakarta.persistence.*

@Entity
@Table(indexes = [Index(name = "i_hash_tag", columnList = "hashTag")])
class HashTag constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val topic: Topic,

    val hashTag: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hash_tag_id")
    val id: Long = 0L
) {
}
