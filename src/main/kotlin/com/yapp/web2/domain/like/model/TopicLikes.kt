package com.yapp.web2.domain.like.model

import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.topic.model.Topic
import jakarta.persistence.*

@Entity
class TopicLikes constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val likedBy: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val topic: Topic,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_likes_id")
    val id: Long = 0L,
) {

}
