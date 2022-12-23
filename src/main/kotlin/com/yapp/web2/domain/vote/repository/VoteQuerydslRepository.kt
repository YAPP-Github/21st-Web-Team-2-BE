package com.yapp.web2.domain.vote.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
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

    fun searchBySlice(lastVoteId: Long? = null, pageable: Pageable): Slice<Vote>? {
        val results: MutableList<Vote> = queryFactory.selectFrom(vote)
            .where(ltVoteId(lastVoteId)) //no-offset paging
            .orderBy(vote.createdAt.desc())
            .limit((pageable.pageSize + 1).toLong())
            .fetch()

        return checkLastPage(pageable, results)
    }

    private fun ltVoteId(voteId: Long?): BooleanExpression? {
        return voteId?.let {
            vote.id.lt(voteId)
        }
    }

    // 무한 스크롤 방식 처리하는 메서드
    private fun checkLastPage(pageable: Pageable, results: MutableList<Vote>): Slice<Vote> {
        var hasNext = false

        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, next = true
        if (results.size > pageable.pageSize) {
            hasNext = true
            results.removeAt(pageable.pageSize)
        }
        return SliceImpl(results, pageable, hasNext)
    }
}
