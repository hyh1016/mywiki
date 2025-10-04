package com.yhproject.mywiki.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yhproject.mywiki.auth.CustomOAuth2UserService
import com.yhproject.mywiki.auth.PrincipalDetails
import com.yhproject.mywiki.config.SecurityConfig
import com.yhproject.mywiki.domain.user.Role
import com.yhproject.mywiki.domain.user.User
import com.yhproject.mywiki.dto.SummariesResponse
import com.yhproject.mywiki.dto.SummaryCreateRequest
import com.yhproject.mywiki.dto.SummaryResponse
import com.yhproject.mywiki.dto.UpdateSummaryRequest
import com.yhproject.mywiki.service.SummaryService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@WebMvcTest(controllers = [SummaryController::class])
@Import(SecurityConfig::class)
class SummaryControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var summaryService: SummaryService

    @MockitoBean
    private lateinit var customOAuth2UserService: CustomOAuth2UserService

    private lateinit var principal: PrincipalDetails
    private lateinit var testUser: User

    @BeforeEach
    fun setUp() {
        testUser = User(
            id = 1L,
            name = "testuser",
            email = "test@email.com",
            role = Role.USER,
            provider = "google",
            providerId = "12345"
        )
        principal = PrincipalDetails(testUser, emptyMap(), "sub")
    }

    @Test
    @DisplayName("요약 생성 요청을 보내면 생성된 요약 정보를 반환한다")
    fun `createSummary returns created summary`() {
        // given
        val request = SummaryCreateRequest(bookmarkId = 1L, content = "Test summary content")
        val response = SummaryResponse(1L, 1L, "Test summary content", LocalDateTime.now().toString())

        whenever(summaryService.createSummary(request, testUser.id)).thenReturn(response)

        // when & then
        mockMvc.perform(
            post("/api/summaries")
                .with(user(principal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(response)))

        verify(summaryService).createSummary(request, testUser.id)
    }

    @Test
    @DisplayName("요약 수정 요청을 보내면 수정된 요약 정보를 반환한다")
    fun `updateSummary returns updated summary`() {
        // given
        val summaryId = 1L
        val request = UpdateSummaryRequest(content = "Updated summary content")
        val response = SummaryResponse(summaryId, 1L, "Updated summary content", LocalDateTime.now().toString())

        whenever(summaryService.updateSummary(summaryId, request, testUser.id)).thenReturn(response)

        // when & then
        mockMvc.perform(
            put("/api/summaries/{summaryId}", summaryId)
                .with(user(principal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(response)))

        verify(summaryService).updateSummary(summaryId, request, testUser.id)
    }

    @Test
    @DisplayName("사용자 ID로 요약 목록 조회를 요청하면 해당 사용자의 요약 목록을 반환한다")
    fun `getSummariesByUser returns list of summaries`() {
        // given
        val userId = testUser.id
        val summaryList = listOf(
            SummaryResponse(1L, 1L, "Summary 1", LocalDateTime.now().toString()),
            SummaryResponse(2L, 2L, "Summary 2", LocalDateTime.now().toString())
        )
        val response = SummariesResponse(summaryList)
        whenever(summaryService.getSummariesByUserId(userId)).thenReturn(response)

        // when & then
        mockMvc.perform(
            get("/api/summaries")
                .param("userId", userId.toString())
                .with(user(principal))
        )
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(response)))

        verify(summaryService).getSummariesByUserId(userId)
    }

    @Test
    @DisplayName("북마크 ID로 요약 조회를 요청하면 해당 북마크의 요약을 반환한다")
    fun `getSummaryByBookmark returns a summary`() {
        // given
        val bookmarkId = 1L
        val response = SummaryResponse(1L, bookmarkId, "Test summary", LocalDateTime.now().toString())
        whenever(summaryService.getSummaryByBookmarkId(bookmarkId, testUser.id)).thenReturn(response)

        // when & then
        mockMvc.perform(
            get("/api/summaries")
                .param("bookmarkId", bookmarkId.toString())
                .with(user(principal))
        )
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(response)))

        verify(summaryService).getSummaryByBookmarkId(bookmarkId, testUser.id)
    }

    @Test
    @DisplayName("인증 없이 요약 생성을 요청하면 401 에러를 반환한다")
    fun `createSummary without auth returns 401`() {
        // given
        val request = SummaryCreateRequest(bookmarkId = 1L, content = "Test content")

        // when & then
        mockMvc.perform(
            post("/api/summaries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("인증 없이 요약 목록 조회를 요청하면 401 에러를 반환한다")
    fun `getSummaries without auth returns 401`() {
        // when & then
        mockMvc.perform(get("/api/summaries").param("userId", "1"))
            .andExpect(status().isUnauthorized)
    }
}