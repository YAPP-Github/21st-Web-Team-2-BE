package com.yapp.web2.web.api.controller.topic

import com.fasterxml.jackson.databind.ObjectMapper
import com.yapp.web2.common.EntityFactory
import com.yapp.web2.common.TestMember
import com.yapp.web2.domain.member.repository.MemberRepository
import com.yapp.web2.domain.topic.model.Topic
import com.yapp.web2.domain.topic.model.TopicCategory
import com.yapp.web2.domain.topic.model.VoteType
import com.yapp.web2.domain.topic.model.option.VoteOption
import com.yapp.web2.domain.topic.model.option.VoteOptionMember
import com.yapp.web2.domain.topic.repository.TopicRepository
import com.yapp.web2.web.api.controller.ApiControllerTest
import com.yapp.web2.web.dto.jwt.response.JwtTokens
import com.yapp.web2.web.dto.topic.request.TopicPostRequest
import com.yapp.web2.web.dto.voteoption.request.VoteOptionPostRequest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
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

    private val jwtTokens = JwtTokens("access-token", "refresh-token")
    private val testMemberA = EntityFactory.testMemberA()

    @BeforeAll
    fun dataInsert() {
        topics = saveDummyTopicsDetail(15)
    }

    @Test
    fun `인기 투표 게시글 조회 API`() {
        val uri = "$uri/popular"
        mockMvc.perform(
            get(uri)
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
            get(uri)
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
            get(uri)
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
            get(uri, findTopicId)
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
            get(uri, findTopicId)
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

    @Test
    fun `투표게시글 최신순 조회 API, 카테고리 필터 적용`() {
        val createdBy = topics[0].createdBy
        val topics = listOf(
            Topic("Topic CareerA", TopicCategory.CAREER, "Content CareerA", VoteType.TEXT, createdBy = createdBy),
            Topic("Topic CareerB", TopicCategory.CAREER, "Content CareerB", VoteType.TEXT, createdBy = createdBy),
            Topic("Topic CareerC", TopicCategory.CAREER, "Content CareerC", VoteType.TEXT, createdBy = createdBy),
        )
        for (topic in topics) {
            topic.addVoteOption(VoteOption("${topic.contents} OptionA", null, null, topic))
            topic.addVoteOption(VoteOption("${topic.contents} OptionB", null, null, topic))
        }

        topicRepository.saveAll(topics)

        val uri = "$uri/latest"
        mockMvc.perform(
            get(uri)
                .param("topicCategory", "CAREER")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andDo(print())
            .andDo(
                document(
                    "get-latest-topic-category",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    queryParameters(
                        parameterWithName("topicCategory").description("투표 게시글 카테고리").optional()
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
    @TestMember
    fun `투표게시글 등록 API 테스트`() {
        val topicPostRequest = TopicPostRequest(
            "TopicA",
            "Contents A",
            listOf(
                VoteOptionPostRequest("OptionA", null, null),
                VoteOptionPostRequest("OptionB", null, null),
            ),
            TopicCategory.DEVELOPER,
            listOf("tagA", "tagB")
        )

        val uri = "$uri"
        mockMvc.perform(
            post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(topicPostRequest))
                .header("Authorization", jwtTokens.accessToken)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andDo(print())
            .andDo(
                document(
                    "post-topic",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("회원 AccessToken")
                    ),
                    requestFields(
                        *topicPostRequestFieldsSnippet()
                    ).andWithPrefix("voteOptions[].", *voteOptionPostRequestFieldsSnippet()),
                    responseFields(
                        beneathPath("data").withSubsectionId("data"),
                        *topicPostResponseFieldsSnippet(),
                    )
                ),
            )
    }

    @Test
    @TestMember
    fun `투표게시글 등록시 필수값이 누락된 경우 예외 발생`() {
        memberRepository.save(testMemberA)

        val topicPostRequest = TopicPostRequest(
            null,
            "Contents A",
            listOf(
                VoteOptionPostRequest(null, null, null),
                VoteOptionPostRequest("OptionB", null, null),
            ),
            TopicCategory.DEVELOPER,
            listOf("tagA", "tagB")
        )

        val uri = "$uri"
        mockMvc.perform(
            post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(topicPostRequest))
                .header("Authorization", jwtTokens.accessToken)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("필수값이 포함되지 않았습니다."))
            .andDo(print())
            .andDo(
                document(
                    "post-topic-required-value-exception",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestHeaders(
                        headerWithName("Authorization").description("회원 AccessToken")
                    ),
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
            fieldWithPath("topicCategory").description("투표 게시글 카테고리"),
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
            fieldWithPath("topicCategory").description("투표 게시글 카테고리"),
            fieldWithPath("commentAmount").description("투표 게시글 댓글 수"),
            fieldWithPath("voteAmount").description("투표 참여 수"),
            fieldWithPath("likeAmount").description("좋아요 수"),
            fieldWithPath("tags").description("태그"),
            subsectionWithPath("voteOptions").description("투표 게시글 선택지 내용"),
        )
    }

    private fun memberPreviewDataResponseFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("member.memberId").description("작성자 Id"),
            fieldWithPath("member.nickname").description("작성자 닉네임"),
            fieldWithPath("member.profileImage").type(JsonFieldType.STRING).description("작성자 프로필 이미지").optional(),
            fieldWithPath("member.jobCategory").description("작성자 직군"),
            fieldWithPath("member.workingYears").description("작성자 연차"),
        )
    }

    private fun voteOptionPreviewDataResponseFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("voteOptionId").description("투표 선택지 Id"),
            fieldWithPath("text").description("투표 선택지 텍스트"),
            fieldWithPath("image").type(JsonFieldType.STRING).description("투표 선택지 이미지").optional(),
            fieldWithPath("codeBlock").type(JsonFieldType.STRING).description("투표 선택지 코드블럭").optional(),
            fieldWithPath("voted").description("현재 사용자의 투표 선택지 투표 여부"),
            fieldWithPath("voteAmount").description("투표 선택지 투표 수"),
        )
    }

    private fun topicPostRequestFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("title").description("투표 게시글 제목"),
            fieldWithPath("contents").description("투표 게시글 내용"),
            fieldWithPath("topicCategory").description("투표 게시글 카테고리"),
            subsectionWithPath("voteOptions").description("투표 선택지"),
            fieldWithPath("tags[]").description("태그").optional(),
        )
    }

    private fun voteOptionPostRequestFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("text").description("투표 선택지 텍스트"),
            fieldWithPath("image").type(JsonFieldType.STRING).description("투표 선택지 이미지").optional(),
            fieldWithPath("codeBlock").type(JsonFieldType.STRING).description("투표 선택지 코드블럭").optional(),
        )
    }

    private fun topicPostResponseFieldsSnippet(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("topicId").description("투표 게시글 Id"),
            fieldWithPath("title").description("투표 게시글 제목"),
            fieldWithPath("voteType").description("투표 게시글 형식"),
            subsectionWithPath("postMemberNickname").description("투표 게시글 작성자 닉네임"),
        )
    }


    // 테스트용 데이터 저장
    private fun saveDummyTopicsDetail(amount: Int): MutableList<Topic> {
        val memberA = EntityFactory.testMemberA()

        memberRepository.saveAll(listOf(memberA))

        val sampleTopics: MutableList<Topic> = mutableListOf()
        for (i in 1..amount) {
            sampleTopics.add(Topic("Vote$i", TopicCategory.DEVELOPER, "Content$i", VoteType.TEXT, createdBy = memberA))
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
