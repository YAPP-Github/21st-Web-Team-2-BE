package com.yapp.web2.web.api.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
class ApiControllerTest constructor(
    protected val uri: String?
) {
    @Autowired
    protected lateinit var mockMvc: MockMvc
}
