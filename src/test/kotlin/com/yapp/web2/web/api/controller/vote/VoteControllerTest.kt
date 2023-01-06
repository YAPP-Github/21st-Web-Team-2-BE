package com.yapp.web2.web.api.controller.vote

import com.yapp.web2.domain.member.model.JobCategory
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.vote.model.Vote
import com.yapp.web2.domain.vote.model.VoteType
import com.yapp.web2.domain.vote.model.option.VoteOption
import com.yapp.web2.domain.vote.model.option.VoteOptionMember
import com.yapp.web2.domain.vote.repository.VoteRepository
import com.yapp.web2.web.api.controller.ApiControllerTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.payload.ResponseFieldsSnippet
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class VoteControllerTest @Autowired constructor(
    val voteRepository: VoteRepository,
    val memberRepository: MemberRepository,
) : ApiControllerTest(uri = "/api/v1/vote") {

    @BeforeAll
    fun dataInsert() {
        saveDummyVotesDetail(15)
    }

    @Test
    fun getPopularVoteTest() {
        val uri = "$uri/popular"
        mockMvc.perform(
            RestDocumentationRequestBuilders.get(uri)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andDo(print())
            .andDo(
                document(
                    "get-popular-vote", // docs directory name
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        *votePreviewDataResponseFieldsSnippet(),
                        fieldWithPath("liked").description("투표 게시글 좋아요 여부")
                    )
                )
            )
    }

    @Test
    fun getLatestVoteNoOffsetTest() {
        val uri = "$uri/latest"
        mockMvc.perform(
            RestDocumentationRequestBuilders.get(uri)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andDo(print())
            .andDo(
                document(
                    "get-latest-vote", // docs directory name
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        *votePreviewDataResponseFieldsSnippet(),
                        fieldWithPath("liked").description("투표 게시글 좋아요 여부")
                    )
                )
            )
    }

    @Test
    fun getLatestVoteOffsetTest() {
        val uri = "$uri/latest"
        mockMvc.perform(
            RestDocumentationRequestBuilders.get(uri)
                .param("lastOffset", "3")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andDo(print())
            .andDo(
                document(
                    "get-latest-vote-offset",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    queryParameters(
                        parameterWithName("lastOffset").description("마지막 투표 게시글 Id").optional()
                    ),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        *votePreviewDataResponseFieldsSnippet(),
                        fieldWithPath("liked").description("투표 게시글 좋아요 여부")
                    )
                ),
            )
    }

    @Test
    fun getVoteDetailTest() {
        val findVoteId = 3L
        val uri = "$uri/$findVoteId"
        mockMvc.perform(
            RestDocumentationRequestBuilders.get(uri)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andDo(print())
            .andDo(
                document(
                    "get-vote-detail",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        *votePreviewDataResponseFieldsSnippet(),
                        fieldWithPath("liked").description("투표 게시글 좋아요 여부")
                    ).andWithPrefix("voteOptionPreviewResponse[].", *voteOptionPreviewDataResponseFieldsSnippet())
                ),
            )
    }

    // 투표 게시글 미리보기 응답에 대한 Spring Rest Docs snippet
    private fun votePreviewDataResponseFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("voteId").description("투표 게시글 Id"),
            fieldWithPath("title").description("투표 게시글 제목"),
            fieldWithPath("contents").description("투표 게시글 내용"),
            fieldWithPath("createdMemberId").description("작성자 Id"),
            fieldWithPath("createdMemberName").description("작성자 닉네임"),
            fieldWithPath("createdMemberProfileImage").description("작성자 프로필 이미지"),
            fieldWithPath("commentAmount").description("투표 게시글 댓글 수"),
            fieldWithPath("voteAmount").description("투표 참여 수"),
            subsectionWithPath("voteOptionPreviewResponse").description("투표 게시글 선택지 내용"),
        )
    }

    private fun voteOptionPreviewDataResponseFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("text").description("투표 선택지 텍스트").optional(),
            fieldWithPath("voteOptionImageFilename").description("투표 선택지 이미지").optional(),
            fieldWithPath("codeBlock").description("투표 선택지 코드블럭").optional(),
            fieldWithPath("voted").description("현재 사용자의 투표 선택지 투표 여부"),
            fieldWithPath("votedAmount").description("투표 선택지 투표 수"),
        )
    }


    // 테스트용 데이터 저장
    private fun saveDummyVotesDetail(amount: Int) {
        val memberA = Member("MemberA", JobCategory.DEVELOPER, 3)
        memberRepository.saveAll(listOf(memberA))

        val sampleVotes: MutableList<Vote> = mutableListOf()
        for (i in 1..amount) {
            sampleVotes.add(Vote("Vote$i", JobCategory.DEVELOPER, "Content$i", VoteType.TEXT, createdBy = memberA))
        }

        for (vote in sampleVotes) {
            vote.addVoteOption(VoteOption("${vote.contents} OptionA", null, null, vote))
            vote.addVoteOption(VoteOption("${vote.contents} OptionB", null, null, vote))
        }

        // 투표 게시글 크기의 2배 만큼 투표수를 받음
        // ex) voteSize = 10, 게시글의 투표수는 20, 18, 16, ... 씩 줄어듦
        for (i in 0 until sampleVotes.size) {
            val voteOptionA = sampleVotes[i].voteOptions[0]
            val voteOptionB = sampleVotes[i].voteOptions[1]

            for (j in 0..i) {
                voteOptionA.addVoteOptionMember(VoteOptionMember(memberA, voteOptionA))
                voteOptionB.addVoteOptionMember(VoteOptionMember(memberA, voteOptionB))
            }
        }
        voteRepository.saveAll(sampleVotes)
    }
}
