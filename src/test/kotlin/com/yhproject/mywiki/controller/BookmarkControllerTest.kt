package com.yhproject.mywiki.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yhproject.mywiki.auth.CustomOAuth2UserService
import com.yhproject.mywiki.auth.WithMockCustomUser
import com.yhproject.mywiki.config.SecurityConfig
import com.yhproject.mywiki.domain.bookmark.Bookmark
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
        val bookmark = Bookmark(
            id = 1L,
            userId = 1L,
            url = "https://example.com",
            title = "Test Title",
            description = "Test Description",
            image = "image.png"
        )
        val response = BookmarkResponse.from(bookmark)
        whenever(bookmarkService.createBookmark(any(), any())).thenReturn(bookmark)

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
        val bookmarks = listOf(
            Bookmark(
                id = 1L,
                userId = 1L,
                url = "https://example.com",
                title = "Test Title",
                description = "Test Description",
                image = "image.png"
            ),
            Bookmark(
                id = 2L,
                userId = 1L,
                url = "https://example2.com",
                title = "Test Title 2",
                description = "Test Description 2",
                image = "image2.png"
            )
        )
        val response = BookmarksResponse.from(bookmarks)
        whenever(bookmarkService.getBookmarks(any())).thenReturn(bookmarks)

        // when & then
        mockMvc.perform(
            get("/api/bookmarks")
        )
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(response)))
    }

    @Test
    @WithMockCustomUser
    @DisplayName("특정 북마크 조회 요청을 보내면 북마크 정보를 반환한다")
    fun `getBookmark returns a bookmark`() {
        // given
        val bookmarkId = 1L
        val bookmark = Bookmark(
            id = bookmarkId,
            userId = 1L,
            url = "https://example.com",
            title = "Test Title",
            description = "Test Description",
            image = "image.png"
        )
        val response = BookmarkResponse.from(bookmark)
        whenever(bookmarkService.getBookmark(any(), any())).thenReturn(bookmark)

        // when & then
        mockMvc.perform(
            get("/api/bookmarks/{bookmarkId}", bookmarkId)
        )
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(response)))
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
