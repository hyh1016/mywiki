package com.yhproject.mywiki.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yhproject.mywiki.auth.CustomOAuth2UserService
import com.yhproject.mywiki.auth.WithMockCustomUser
import com.yhproject.mywiki.config.SecurityConfig
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [UserController::class])
@Import(SecurityConfig::class)
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var customOAuth2UserService: CustomOAuth2UserService

    @Test
    @WithMockCustomUser(role = "USER")
    @DisplayName("내 정보를 요청하면 세션에 저장된 사용자 정보를 반환한다")
    fun `getMyInfo returns user info from session`() {
        // when & then
        mockMvc.perform(
            get("/api/user/me")
        )
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("세션에 사용자 정보가 없으면 null을 반환한다")
    fun `getMyInfo returns null if no user in session`() {
        // when & then
        mockMvc.perform(get("/api/user/me")) // 세션 없이 호출
            .andExpect(status().isUnauthorized)
    }
}
