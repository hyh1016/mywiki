package com.yhproject.mywiki.controller

import com.yhproject.mywiki.auth.LoginUser
import com.yhproject.mywiki.auth.SessionUser
import com.yhproject.mywiki.dto.BookmarkCreateRequest
import com.yhproject.mywiki.dto.BookmarkResponse
import com.yhproject.mywiki.dto.BookmarksResponse
import com.yhproject.mywiki.service.BookmarkService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bookmarks")
class BookmarkController(
    private val bookmarkService: BookmarkService
) {

    @PostMapping
    fun createBookmark(
        @RequestBody request: BookmarkCreateRequest,
        @LoginUser sessionUser: SessionUser
    ): ResponseEntity<BookmarkResponse> {
        val bookmark = bookmarkService.createBookmark(request, sessionUser.id)
        return ResponseEntity.ok(BookmarkResponse.from(bookmark))
    }

    @GetMapping
    fun getBookmarks(@LoginUser sessionUser: SessionUser): ResponseEntity<BookmarksResponse> {
        val bookmarks = bookmarkService.getBookmarks(sessionUser.id)
        return ResponseEntity.ok(BookmarksResponse.from(bookmarks))
    }

    @GetMapping("/{bookmarkId}")
    fun getBookmark(
        @PathVariable bookmarkId: Long,
        @LoginUser sessionUser: SessionUser
    ): ResponseEntity<BookmarkResponse> {
        val bookmark = bookmarkService.getBookmark(bookmarkId, sessionUser.id)
        return ResponseEntity.ok(BookmarkResponse.from(bookmark))
    }

    @GetMapping("/random")
    fun getRandomBookmarks(@LoginUser sessionUser: SessionUser): ResponseEntity<BookmarkResponse> {
        val bookmark = bookmarkService.getRandomBookmark(sessionUser.id)
        return ResponseEntity.ok(BookmarkResponse.from(bookmark))
    }
}
