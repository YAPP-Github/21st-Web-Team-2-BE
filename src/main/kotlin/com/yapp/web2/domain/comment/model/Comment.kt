package com.yapp.web2.domain.comment.model

import com.yapp.web2.common.entity.BaseEntity
import com.yapp.web2.domain.like.model.CommentLikes
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.topic.model.Topic
import jakarta.persistence.*
import org.hibernate.annotations.Where

@Entity
@Where(clause = "status = \'ACTIVE\'")
class Comment constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val createdBy: Member,

    var contents: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val topic: Topic,

    @OneToMany(mappedBy = "comment")
    val replyComments: MutableList<ReplyComment> = mutableListOf(),

    @OneToMany(mappedBy = "comment", cascade = [CascadeType.ALL])
    val commentLikes: MutableList<CommentLikes> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    val id: Long = 0L,
) : BaseEntity() {

    fun addCommentLikes(commentLike: CommentLikes) {
        this.commentLikes.add(commentLike)
    }
}
