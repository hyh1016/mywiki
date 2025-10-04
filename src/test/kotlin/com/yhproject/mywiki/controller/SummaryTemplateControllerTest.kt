
package com.yhproject.mywiki.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yhproject.mywiki.auth.CustomOAuth2UserService
import com.yhproject.mywiki.auth.PrincipalDetails
import com.yhproject.mywiki.config.SecurityConfig
import com.yhproject.mywiki.domain.summary.SummaryTemplate
import com.yhproject.mywiki.domain.summary.SummaryTemplateSection
import com.yhproject.mywiki.domain.user.Role
import com.yhproject.mywiki.domain.user.User
import com.yhproject.mywiki.dto.SummaryTemplateResponse
import com.yhproject.mywiki.service.SummaryService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [SummaryTemplateController::class])
@Import(SecurityConfig::class)
class SummaryTemplateControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var summaryService: SummaryService

    @MockitoBean
    private lateinit var customOAuth2UserService: CustomOAuth2UserService

    private lateinit var principal: PrincipalDetails

    @BeforeEach
    fun setUp() {
        val user = User(
            id = 1L,
            name = "testuser",
            email = "test@email.com",
            role = Role.USER,
            provider = "google",
            providerId = "12345"
        )
        principal = PrincipalDetails(user, emptyMap(), "sub")
    }

    @Test
    @DisplayName("요약 템플릿 조회 요청을 보내면 템플릿 목록을 반환한다")
    fun `getSummaryTemplates returns template list`() {
        // given
        val templates = listOf(
            SummaryTemplate(
                id = 1L,
                section = SummaryTemplateSection.BIG_PICTURE,
                title = "핵심 파악",
                description = "한 문장으로 요약"
            )
        )
        val response = SummaryTemplateResponse.from(templates)

        given(summaryService.getSummaryTemplates()).willReturn(response)

        // when & then
        mockMvc.perform(
            get("/api/summary-templates")
                .with(user(principal))
        )
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(response)))

        verify(summaryService).getSummaryTemplates()
    }

    @Test
    @DisplayName("인증 없이 요약 템플릿 조회를 요청하면 401 에러를 반환한다")
    fun `getSummaryTemplates without auth returns 401`() {
        // when & then
        mockMvc.perform(get("/api/summary-templates"))
            .andExpect(status().isUnauthorized)
    }
}
