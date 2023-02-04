package com.yapp.web2.web.api.controller.member

import com.yapp.web2.common.annotation.CurrentMember
import com.yapp.web2.domain.member.application.MemberService
import com.yapp.web2.domain.member.model.Member
import com.yapp.web2.web.api.response.ApiResponse
import com.yapp.web2.web.dto.member.request.NicknameDuplicationRequest
import com.yapp.web2.web.dto.member.response.NicknameDuplicationResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class MemberController(
    private val memberService: MemberService
) {
    @PostMapping("/nickname-duplication")
    fun existsNickname(@RequestBody nicknameDuplicationRequest: NicknameDuplicationRequest): ApiResponse<NicknameDuplicationResponse> {
        return ApiResponse.success(memberService.existsByNickname(nicknameDuplicationRequest))
    }

    @GetMapping("/member/{memberId}", "/member")
    fun getMember(
        @CurrentMember member: Member,
        @PathVariable(required = false, name = "memberId") memberId: Long?
    ) =
        ApiResponse.success(
            memberId?.let {
                memberService.getMember(memberId)
            } ?: memberService.getMember(member.id)
        )
}
