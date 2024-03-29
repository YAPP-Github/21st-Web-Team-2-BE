package com.yapp.web2.domain.topic.model.option

import com.yapp.web2.common.entity.BaseEntity
import com.yapp.web2.domain.topic.model.Topic
import jakarta.persistence.*
import org.hibernate.annotations.Where

@Entity
@Where(clause = "status = \'ACTIVE\'")
class VoteOption constructor(
    val text: String?,

    val image: String?,

    val language: String?,
    val codeBlock: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val topic: Topic,

    @OneToMany(mappedBy = "voteOption", cascade = [CascadeType.PERSIST])
    val voteOptionMembers: MutableList<VoteOptionMember> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_option_id")
    val id: Long = 0L
) : BaseEntity() {
    //TODO text, image, codeblock이 모두 null인 경우에 대한 검증 메서드 필요

    fun addVoteOptionMember(voteOptionMember: VoteOptionMember) {
        this.voteOptionMembers.add(voteOptionMember)
    }
}
