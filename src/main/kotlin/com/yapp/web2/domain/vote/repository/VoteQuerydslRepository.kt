package com.yapp.web2.domain.vote.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.yapp.web2.domain.comment.model.QComment.comment
import com.yapp.web2.domain.member.model.QMember.member
import com.yapp.web2.domain.vote.model.QVote.vote
import com.yapp.web2.domain.vote.model.Vote
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Component

@Component
class VoteQuerydslRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findMainPageVotes(lastVoteId: Long? = null, pageable: Pageable): Slice<Vote> {
        val results: MutableList<Vote> = queryFactory.select(vote)
            .from(vote)
            .distinct()
            .where(lastVoteId?.let { vote.id.lt(lastVoteId) })
            .orderBy(vote.createdAt.desc())
            .limit((pageable.pageSize + 1).toLong())
            .join(vote.createdBy, member).fetchJoin()
            .leftJoin(vote.comments).fetchJoin()
            .leftJoin(vote.voteOptions).fetchJoin()
            .fetch()

        return SliceImpl(results, pageable, hasNext(pageable, results))
    }

    private fun hasNext(pageable: Pageable, results: MutableList<Vote>): Boolean {
        return if (results.size > pageable.pageSize) {
            results.removeAt(pageable.pageSize)
            true
        } else {
            false
        }
    }
}
