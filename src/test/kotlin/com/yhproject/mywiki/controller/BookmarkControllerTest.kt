package com.yhproject.mywiki.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yhproject.mywiki.auth.CustomOAuth2UserService
import com.yhproject.mywiki.auth.PrincipalDetails
import com.yhproject.mywiki.config.SecurityConfig
import com.yhproject.mywiki.domain.user.Role
import com.yhproject.mywiki.domain.user.User
import com.yhproject.mywiki.dto.BookmarkCreateRequest
import com.yhproject.mywiki.dto.BookmarkResponse
import com.yhproject.mywiki.service.BookmarkService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
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
    @DisplayName("북마크 생성 요청을 보내면 생성된 북마크 정보를 반환한다")
    fun `createBookmark returns created bookmark`() {
        // given
        val sessionUser = principal.sessionUser
        val request = BookmarkCreateRequest(url = "https://example.com")
        val response = BookmarkResponse(1L, "https://example.com", "Test Title", "Test Description", "image.png")

        given(bookmarkService.createBookmark(request, sessionUser)).willReturn(response)

        // when & then
        mockMvc.perform(
            post("/api/bookmarks")
                .with(user(principal))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(response)))

        verify(bookmarkService).createBookmark(request, sessionUser)
    }

    @Test
    @DisplayName("북마크 목록 조회 요청을 보내면 북마크 리스트를 반환한다")
    fun `getBookmarks returns bookmark list`() {
        // given
        val sessionUser = principal.sessionUser
        val bookmarks = listOf(
            BookmarkResponse(1L, "https://example.com", "Test Title", "Test Description", "image.png"),
            BookmarkResponse(2L, "https://example2.com", "Test Title 2", "Test Description 2", "image2.png")
        )
        given(bookmarkService.getBookmarks(sessionUser)).willReturn(bookmarks)

        // when & then
        mockMvc.perform(
            get("/api/bookmarks")
                .with(user(principal))
        )
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(bookmarks)))

        verify(bookmarkService).getBookmarks(sessionUser)
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
