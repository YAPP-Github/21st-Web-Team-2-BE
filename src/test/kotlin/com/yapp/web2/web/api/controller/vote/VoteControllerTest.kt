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
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.payload.ResponseFieldsSnippet
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.restdocs.snippet.Attributes.attributes
import org.springframework.restdocs.snippet.Attributes.key
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class VoteControllerTest @Autowired constructor(
    val voteRepository: VoteRepository,
    val memberRepository: MemberRepository,
) : ApiControllerTest(uri = "/api/v1/vote") {

    lateinit var votes: MutableList<Vote>
    @BeforeAll
    fun dataInsert() {
        votes = saveDummyVotesDetail(15)
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
                    ).andWithPrefix("voteOptions[].", *voteOptionPreviewDataResponseFieldsSnippet())
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
                    ).andWithPrefix("voteOptions[].", *voteOptionPreviewDataResponseFieldsSnippet())
                )
            )
    }

    @Test
    fun getLatestVoteOffsetTest() {
        val uri = "$uri/latest"
        mockMvc.perform(
            RestDocumentationRequestBuilders.get(uri)
                .param("lastOffset", "${votes.last().id}")
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
                    ).andWithPrefix("voteOptions[].", *voteOptionPreviewDataResponseFieldsSnippet())
                ),
            )
    }

    @Test
    fun getVoteDetailTest() {
        val findVoteId = 2L
        val uri = "$uri/{voteId}"
        mockMvc.perform(
            RestDocumentationRequestBuilders.get(uri, findVoteId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andDo(print())
            .andDo(
                document(
                    "get-vote-detail",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("voteId").description("투표 게시글 Id")
                    ),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        *votePreviewDataResponseFieldsSnippet(),
                        fieldWithPath("liked").description("투표 게시글 좋아요 여부")
                    ).andWithPrefix("voteOptions[].", *voteOptionPreviewDataResponseFieldsSnippet())
                ),
            )
    }

    @Test
    fun getVoteDetailFailTest() {
        val findVoteId = 12450L
        val uri = "$uri/{voteId}"
        mockMvc.perform(
            RestDocumentationRequestBuilders.get(uri, findVoteId)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("존재하지 않는 리소스 요청입니다."))
            .andDo(print())
            .andDo(
                document(
                    "get-vote-detail-fail",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("code").description("요청 결과 상태 코드"),
                        fieldWithPath("message").description("상태 메세지"),
                    )
                ),
            )
    }

    // 투표 게시글 미리보기 응답에 대한 Spring Rest Docs snippet
    private fun votePreviewDataResponseFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("topicId").description("투표 게시글 Id"),
            fieldWithPath("title").description("투표 게시글 제목"),
            fieldWithPath("contents").description("투표 게시글 내용"),
            fieldWithPath("memberId").description("작성자 Id"),
            fieldWithPath("memberName").description("작성자 닉네임"),
            fieldWithPath("memberProfileImage").type(JsonFieldType.STRING).description("작성자 프로필 이미지").optional(),
            fieldWithPath("commentAmount").description("투표 게시글 댓글 수"),
            fieldWithPath("voteAmount").description("투표 참여 수"),
            subsectionWithPath("voteOptions").description("투표 게시글 선택지 내용"),
        )
    }

    private fun voteOptionPreviewDataResponseFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("text").description("투표 선택지 텍스트"),
            fieldWithPath("voteOptionImageFilename").type(JsonFieldType.STRING).description("투표 선택지 이미지").optional(),
            fieldWithPath("codeBlock").type(JsonFieldType.STRING).description("투표 선택지 코드블럭").optional(),
            fieldWithPath("voted").description("현재 사용자의 투표 선택지 투표 여부"),
            fieldWithPath("votedAmount").description("투표 선택지 투표 수"),
        )
    }


    // 테스트용 데이터 저장
    private fun saveDummyVotesDetail(amount: Int): MutableList<Vote> {
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
        return voteRepository.saveAll(sampleVotes)
    }
}
