package com.yhproject.mywiki.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yhproject.mywiki.auth.CustomOAuth2UserService
import com.yhproject.mywiki.auth.SessionUser
import com.yhproject.mywiki.config.SecurityConfig
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.mock.web.MockHttpSession
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [UserController::class])
@Import(SecurityConfig::class)
@WithMockUser(roles = ["USER"])
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var customOAuth2UserService: CustomOAuth2UserService

    private lateinit var sessionUser: SessionUser
    private lateinit var mockHttpSession: MockHttpSession

    @BeforeEach
    fun setUp() {
        sessionUser = SessionUser(1L, "testuser")
        mockHttpSession = MockHttpSession()
        mockHttpSession.setAttribute("user", sessionUser)
    }

    @Test
    @DisplayName("내 정보를 요청하면 세션에 저장된 사용자 정보를 반환한다")
    fun `getMyInfo returns user info from session`() {
        // when & then
        mockMvc.perform(
            get("/api/v1/user/me")
                .session(mockHttpSession)
        )
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(sessionUser)))
    }

    @Test
    @DisplayName("세션에 사용자 정보가 없으면 null을 반환한다")
    fun `getMyInfo returns null if no user in session`() {
        // when & then
        mockMvc.perform(get("/api/v1/user/me")) // 세션 없이 호출
            .andExpect(status().isOk)
            .andExpect(content().string("")) // 컨트롤러가 null을 반환하면 body는 비어있음
    }
}
