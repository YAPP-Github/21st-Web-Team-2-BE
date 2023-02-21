package com.yapp.web2.domain.topic.repository.option

import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.topic.model.option.VoteOption
import com.yapp.web2.domain.topic.model.option.VoteOptionMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface VoteOptionMemberRepository : JpaRepository<VoteOptionMember, Long> {
    fun existsByVoteOptionAndVotedBy(voteOption: VoteOption, member: Member): Boolean

    @Query(value = "select m from VoteOptionMember m join fetch m.votedBy where m.voteOption = :voteOption")
    fun findVotedMembersByVoteOptionId(voteOption: VoteOption) : List<VoteOptionMember>
}
