package com.yhproject.mywiki.auth

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @DisplayName("인증되지 않은 사용자가 보호된 API를 호출하면 401 에러를 반환한다")
    fun `unauthenticated user is redirected to login page`() {
        mockMvc.perform(get("/api/v1/user/me"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(roles = ["USER"])
    @DisplayName("USER 권한의 사용자는 보호된 API를 호출할 수 있다")
    fun `user with USER role can access protected api`() {
        mockMvc.perform(get("/api/v1/user/me"))
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser(roles = ["GUEST"])
    @DisplayName("GUEST 권한의 사용자는 보호된 API를 호출할 수 없다")
    fun `user with GUEST role cannot access protected api`() {
        mockMvc.perform(get("/api/v1/user/me"))
            .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser
    @DisplayName("로그아웃을 호출하면 루트 페이지로 리디렉션된다")
    fun `logout redirects to root page`() {
        mockMvc.perform(get("/logout"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/"))
    }
}
