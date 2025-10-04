package com.yhproject.mywiki.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yhproject.mywiki.auth.CustomOAuth2UserService
import com.yhproject.mywiki.auth.WithMockCustomUser
import com.yhproject.mywiki.config.SecurityConfig
import com.yhproject.mywiki.dto.BookmarkCreateRequest
import com.yhproject.mywiki.dto.BookmarkResponse
import com.yhproject.mywiki.dto.BookmarksResponse
import com.yhproject.mywiki.service.BookmarkService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [BookmarkController::class])
@Import(SecurityConfig::class)
class BookmarkControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var bookmarkService: BookmarkService

    @MockitoBean
    private lateinit var customOAuth2UserService: CustomOAuth2UserService

    @Test
    @WithMockCustomUser(role = "USER")
    @DisplayName("북마크 생성 요청을 보내면 생성된 북마크 정보를 반환한다")
    fun `createBookmark returns created bookmark`() {
        // given
        val request = BookmarkCreateRequest(url = "https://example.com")
        val response = BookmarkResponse(1L, "https://example.com", "Test Title", "Test Description", "image.png")
        whenever(bookmarkService.createBookmark(any(), any())).thenReturn(response)

        // when & then
        mockMvc.perform(
            post("/api/bookmarks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(response)))
    }

    @Test
    @WithMockCustomUser(role = "USER")
    @DisplayName("북마크 목록 조회 요청을 보내면 북마크 리스트를 반환한다")
    fun `getBookmarks returns bookmark list`() {
        // given
        val bookmarks = BookmarksResponse(
            listOf(
                BookmarkResponse(1L, "https://example.com", "Test Title", "Test Description", "image.png"),
                BookmarkResponse(2L, "https://example2.com", "Test Title 2", "Test Description 2", "image2.png")
            )
        )
        whenever(bookmarkService.getBookmarks(any())).thenReturn(bookmarks)

        // when & then
        mockMvc.perform(
            get("/api/bookmarks")
        )
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(bookmarks)))
    }

    @Test
    @DisplayName("인증 정보 없이 북마크 생성을 요청하면 401 에러를 반환한다")
    fun `createBookmark without authentication returns 401`() {
        // given
        val request = BookmarkCreateRequest(url = "https://example.com")

        // when & then
        mockMvc.perform(
            post("/api/bookmarks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("인증 정보 없이 북마크 목록 조회를 요청하면 401 에러를 반환한다")
    fun `getBookmarks without authentication returns 401`() {
        // when & then
        mockMvc.perform(get("/api/bookmarks"))
            .andExpect(status().isUnauthorized)
    }
}