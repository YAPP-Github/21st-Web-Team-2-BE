package com.yapp.web2.web.api.controller.actuator

import com.yapp.web2.web.api.controller.ApiControllerTest
import org.junit.jupiter.api.Test
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class ActuatorControllerTest : ApiControllerTest(uri = "/actuator") {

	@Test
	fun actuatorHealthCheckTest() {
		val uri = "$uri/health"
		mockMvc.perform(
			RestDocumentationRequestBuilders.get(uri)
		)
			.andExpect(MockMvcResultMatchers.status().isOk)
			.andExpect(MockMvcResultMatchers.jsonPath("$.status").value("UP"))
			.andDo(MockMvcResultHandlers.print())
			.andDo(
				document(
					"actuator-health", // docs directory name
					responseFields(
						fieldWithPath("status").description("현재 상태"),
					)
				)
			)
	}

}
