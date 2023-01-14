package com.yapp.web2.domain.topic.model

import com.yapp.web2.web.dto.voteoption.request.VoteOptionPostDto
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class VoteTypeTest {
    @Test
    fun `저장 요청에 따라 옳바른 투표 타입 생성 테스트`() {
        //given
        val voteOptionAll = VoteOptionPostDto("Text", "imageUrl", "codeBlock")
        val voteOptionCode = VoteOptionPostDto("Text", null, "codeBlock")
        val voteOptionImage = VoteOptionPostDto("Text", "imageUrl", null)
        val voteOptionText = VoteOptionPostDto("Text", null, null)

        //when
        assertAll(
            { assertVoteTypeFromDto(voteOptionAll, VoteType.TEXT_IMAGE_CODE_BLOCK) },
            { assertVoteTypeFromDto(voteOptionCode, VoteType.TEXT_CODE_BLOCK) },
            { assertVoteTypeFromDto(voteOptionImage, VoteType.TEXT_IMAGE) },
            { assertVoteTypeFromDto(voteOptionText, VoteType.TEXT) },
        )
    }

    private fun assertVoteTypeFromDto(voteOptionDto: VoteOptionPostDto, expectedType: VoteType) {
        val voteType = VoteType.from(voteOptionDto)
        assertThat(voteType).isEqualTo(expectedType)
    }

}
