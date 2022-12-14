package com.yapp.web2.web.api.controller.topic

import com.yapp.web2.common.EntityFactory
import com.yapp.web2.domain.member.model.JobCategory
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.topic.model.Topic
import com.yapp.web2.domain.topic.model.VoteType
import com.yapp.web2.domain.topic.model.option.VoteOption
import com.yapp.web2.domain.topic.model.option.VoteOptionMember
import com.yapp.web2.domain.topic.repository.TopicRepository
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
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TopicControllerTest @Autowired constructor(
    val topicRepository: TopicRepository,
    val memberRepository: MemberRepository,
) : ApiControllerTest(uri = "/api/v1/topic") {

    lateinit var topics: MutableList<Topic>

    @BeforeAll
    fun dataInsert() {
        topics = saveDummyTopicsDetail(15)
    }

    @Test
    fun `인기 투표 게시글 조회 API`() {
        val uri = "$uri/popular"
        mockMvc.perform(
            RestDocumentationRequestBuilders.get(uri)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andDo(print())
            .andDo(
                document(
                    "get-popular-topic", // docs directory name
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        *topicPreviewDataResponseFieldsSnippet(),
                        *memberPreviewDataResponseFieldsSnippet(),
                    ).andWithPrefix("voteOptions[].", *voteOptionPreviewDataResponseFieldsSnippet())
                )
            )
    }

    @Test
    fun `투표게시글 최신순 조회 API, 첫 페이지`() {
        val uri = "$uri/latest"
        mockMvc.perform(
            RestDocumentationRequestBuilders.get(uri)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andDo(print())
            .andDo(
                document(
                    "get-latest-topic", // docs directory name
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        *topicPreviewDataResponseFieldsSnippet(),
                        *memberPreviewDataResponseFieldsSnippet(),
                    ).andWithPrefix("voteOptions[].", *voteOptionPreviewDataResponseFieldsSnippet())
                )
            )
    }

    @Test
    fun `투표게시글 최신순 조회 API, offset 적용`() {
        val uri = "$uri/latest"
        mockMvc.perform(
            RestDocumentationRequestBuilders.get(uri)
                .param("lastOffset", "${topics.last().id}")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andDo(print())
            .andDo(
                document(
                    "get-latest-topic-offset",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    queryParameters(
                        parameterWithName("lastOffset").description("마지막 투표 게시글 Id").optional()
                    ),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        *topicPreviewDataResponseFieldsSnippet(),
                        *memberPreviewDataResponseFieldsSnippet(),
                    ).andWithPrefix("voteOptions[].", *voteOptionPreviewDataResponseFieldsSnippet())
                ),
            )
    }

    @Test
    fun `투표 게시글 상세 조회 API`() {
        val findTopicId = 2L
        val uri = "$uri/{topicId}"
        mockMvc.perform(
            RestDocumentationRequestBuilders.get(uri, findTopicId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andDo(print())
            .andDo(
                document(
                    "get-topic-detail",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("topicId").description("투표 게시글 Id")
                    ),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        *topicDetailDataResponseFieldsSnippet(),
                        *memberPreviewDataResponseFieldsSnippet(),
                        fieldWithPath("liked").description("투표 게시글 좋아요 여부")
                    ).andWithPrefix("voteOptions[].", *voteOptionPreviewDataResponseFieldsSnippet())
                ),
            )
    }

    @Test
    fun `존재하지 않는 투표 게시글 상세 조회 예외 테스트`() {
        val findTopicId = 12450L
        val uri = "$uri/{topicId}"
        mockMvc.perform(
            RestDocumentationRequestBuilders.get(uri, findTopicId)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("존재하지 않는 리소스 요청입니다."))
            .andDo(print())
            .andDo(
                document(
                    "get-topic-detail-fail",
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
    private fun topicPreviewDataResponseFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("topicId").description("투표 게시글 Id"),
            fieldWithPath("title").description("투표 게시글 제목"),
            fieldWithPath("contents").description("투표 게시글 내용"),
            fieldWithPath("commentAmount").description("투표 게시글 댓글 수"),
            fieldWithPath("voteAmount").description("투표 참여 수"),
            subsectionWithPath("voteOptions").description("투표 게시글 선택지 내용"),
        )
    }

    // 투표 게시글 상세조회 응답에 대한 Spring Rest Docs snippet
    private fun topicDetailDataResponseFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("topicId").description("투표 게시글 Id"),
            fieldWithPath("title").description("투표 게시글 제목"),
            fieldWithPath("contents").description("투표 게시글 내용"),
            fieldWithPath("commentAmount").description("투표 게시글 댓글 수"),
            fieldWithPath("voteAmount").description("투표 참여 수"),
            fieldWithPath("likedAmount").description("좋아요 수"),
            fieldWithPath("tags").description("태그"),
            subsectionWithPath("voteOptions").description("투표 게시글 선택지 내용"),
        )
    }

    private fun memberPreviewDataResponseFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("member.id").description("작성자 Id"),
            fieldWithPath("member.name").description("작성자 닉네임"),
            fieldWithPath("member.profileImage").type(JsonFieldType.STRING).description("작성자 프로필 이미지").optional(),
            fieldWithPath("member.jobCategory").description("작성자 직군"),
            fieldWithPath("member.workingYears").description("작성자 연차"),
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
    private fun saveDummyTopicsDetail(amount: Int): MutableList<Topic> {
        val memberA = EntityFactory.testMemberA()

        memberRepository.saveAll(listOf(memberA))

        val sampleTopics: MutableList<Topic> = mutableListOf()
        for (i in 1..amount) {
            sampleTopics.add(Topic("Vote$i", JobCategory.DEVELOPER, "Content$i", VoteType.TEXT, createdBy = memberA))
        }

        for (topic in sampleTopics) {
            topic.addVoteOption(VoteOption("${topic.contents} OptionA", null, null, topic))
            topic.addVoteOption(VoteOption("${topic.contents} OptionB", null, null, topic))
        }

        // 투표 게시글 크기의 2배 만큼 투표수를 받음
        // ex) topicSize = 10, 게시글의 투표수는 20, 18, 16, ... 씩 줄어듦
        for (i in 0 until sampleTopics.size) {
            val voteOptionA = sampleTopics[i].voteOptions[0]
            val voteOptionB = sampleTopics[i].voteOptions[1]

            for (j in 0..i) {
                voteOptionA.addVoteOptionMember(VoteOptionMember(memberA, voteOptionA))
                voteOptionB.addVoteOptionMember(VoteOptionMember(memberA, voteOptionB))
            }
        }
        return topicRepository.saveAll(sampleTopics)
    }
}
