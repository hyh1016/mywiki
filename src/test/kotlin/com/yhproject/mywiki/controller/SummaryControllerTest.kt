package com.yhproject.mywiki.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yhproject.mywiki.auth.CustomOAuth2UserService
import com.yhproject.mywiki.auth.WithMockCustomUser
import com.yhproject.mywiki.config.SecurityConfig
import com.yhproject.mywiki.domain.bookmark.Bookmark
import com.yhproject.mywiki.domain.summary.*
import com.yhproject.mywiki.dto.SummariesResponse
import com.yhproject.mywiki.dto.SummaryCreateRequest
import com.yhproject.mywiki.dto.SummaryResponse
import com.yhproject.mywiki.dto.UpdateSummaryRequest
import com.yhproject.mywiki.service.SummaryService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

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

    private fun createTestBookmark(id: Long, userId: Long) = Bookmark(
        id = id,
        userId = userId,
        url = "https://example.com/$id",
        title = "Test Title $id",
        description = "Test Description $id",
        image = "image$id.png"
    )

    private fun createTestSummary(id: Long, bookmark: Bookmark, contents: SummaryContents) = Summary(
        id = id,
        bookmark = bookmark,
        contents = contents,
    )

    private fun createTestTemplate(id: Long, section: SummaryTemplateSection, title: String, description: String?) = SummaryTemplate(
        id = id,
        section = section,
        title = title,
        description = description
    )

    @Test
    @WithMockCustomUser(role = "USER")
    @DisplayName("요약 생성 요청을 보내면 생성된 요약 정보를 반환한다")
    fun `createSummary returns created summary`() {
        // given
        val userId = 1L
        val bookmarkId = 1L
        val testContentItems = listOf(SummaryContentItem(id = 1, content = "Test summary content"))
        val request = SummaryCreateRequest(bookmarkId = bookmarkId, contents = testContentItems)
        val bookmark = createTestBookmark(bookmarkId, userId)
        val summary = createTestSummary(1L, bookmark, SummaryContents(testContentItems))
        val templates = listOf(
            createTestTemplate(1, SummaryTemplateSection.BIG_PICTURE, "이 글을 한 문장으로 요약하자면?", "이 글이 소개하고자 했던 개념, 해결하고자 했던 문제 등을 하나의 문장으로 정리해보세요.")
        )
        val response = SummaryResponse.from(summary, templates)

        whenever(summaryService.createSummary(any(), any())).thenReturn(summary)
        whenever(summaryService.getSummaryTemplates()).thenReturn(templates)

        // when & then
        mockMvc.perform(
            post("/api/summaries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(content().json(objectMapper.writeValueAsString(response)))

        verify(summaryService).createSummary(any(), any())
        verify(summaryService).getSummaryTemplates()
    }

    @Test
    @WithMockCustomUser(role = "USER")
    @DisplayName("요약 수정 요청을 보내면 수정된 요약 정보를 반환한다")
    fun `updateSummary returns updated summary`() {
        // given
        val userId = 1L
        val summaryId = 1L
        val bookmarkId = 1L
        val testContentItems = listOf(SummaryContentItem(id = 1, content = "Updated summary content"))
        val request = UpdateSummaryRequest(contents = testContentItems)
        val bookmark = createTestBookmark(bookmarkId, userId)
        val summary = createTestSummary(summaryId, bookmark, SummaryContents(testContentItems))
        val templates = listOf(
            createTestTemplate(1, SummaryTemplateSection.BIG_PICTURE, "이 글을 한 문장으로 요약하자면?", "이 글이 소개하고자 했던 개념, 해결하고자 했던 문제 등을 하나의 문장으로 정리해보세요.")
        )
        val response = SummaryResponse.from(summary, templates)

        whenever(summaryService.updateSummary(any(), any(), any())).thenReturn(summary)
        whenever(summaryService.getSummaryTemplates()).thenReturn(templates)

        // when & then
        mockMvc.perform(
            put("/api/summaries/{summaryId}", summaryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(response)))

        verify(summaryService).updateSummary(any(), any(), any())
        verify(summaryService).getSummaryTemplates()
    }

    @Test
    @WithMockCustomUser(role = "USER")
    @DisplayName("요약 목록 조회를 요청하면 사용자 ID를 통해 해당 사용자의 요약 목록을 반환한다")
    fun `getSummariesByUser returns list of summaries`() {
        // given
        val userId = 1L
        val bookmark1 = createTestBookmark(1L, userId)
        val bookmark2 = createTestBookmark(2L, userId)
        val summaryList = listOf(
            createTestSummary(1L, bookmark1, SummaryContents(listOf(SummaryContentItem(id = 1, content = "Summary 1")))),
            createTestSummary(2L, bookmark2, SummaryContents(listOf(SummaryContentItem(id = 2, content = "Summary 2"))))
        )
        val response = SummariesResponse.from(summaryList)
        whenever(summaryService.getSummariesByUserId(userId)).thenReturn(summaryList)

        // when & then
        mockMvc.perform(
            get("/api/summaries")
        )
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(response)))

        verify(summaryService).getSummariesByUserId(userId)
    }

    @Test
    @WithMockCustomUser(role = "USER")
    @DisplayName("북마크 ID로 요약 조회를 요청하면 해당 북마크의 요약을 반환한다")
    fun `getSummaryByBookmark returns a summary`() {
        // given
        val userId = 1L
        val bookmarkId = 1L
        val bookmark = createTestBookmark(bookmarkId, userId)
        val testContentItems = listOf(SummaryContentItem(id = 3, content = "Test summary"))
        val summary = createTestSummary(1L, bookmark, SummaryContents(testContentItems))
        val templates = listOf(
            createTestTemplate(3, SummaryTemplateSection.DETAILS, "왜-무엇을-어떻게형", "")
        )
        val response = SummaryResponse.from(summary, templates)

        whenever(summaryService.getSummaryByBookmarkId(bookmarkId, userId)).thenReturn(summary)
        whenever(summaryService.getSummaryTemplates()).thenReturn(templates)

        // when & then
        mockMvc.perform(
            get("/api/summaries")
                .param("bookmarkId", bookmarkId.toString())
        )
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(response)))

        verify(summaryService).getSummaryByBookmarkId(bookmarkId, userId)
        verify(summaryService).getSummaryTemplates()
    }

    @Test
    @DisplayName("인증 없이 요약 생성을 요청하면 401 에러를 반환한다")
    fun `createSummary without auth returns 401`() {
        // given
        val testContentItems = listOf(SummaryContentItem(id = 1, content = "Test content"))
        val request = SummaryCreateRequest(bookmarkId = 1L, contents = testContentItems)

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
        mockMvc.perform(get("/api/summaries"))
            .andExpect(status().isUnauthorized)
    }
}
