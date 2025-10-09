package com.yhproject.mywiki.dto

import com.yhproject.mywiki.domain.bookmark.Bookmark
import java.time.LocalDateTime

data class BookmarkCreateRequest(
    val url: String
)

data class BookmarksResponse(
    val bookmarks: List<BookmarkResponse>
) {
    companion object {
        fun from(bookmarks: List<Bookmark>): BookmarksResponse {
            return BookmarksResponse(bookmarks.map { bookmark -> BookmarkResponse.from(bookmark) })
        }
    }
}

data class BookmarkResponse(
    val id: Long,
    val url: String,
    val title: String,
    val description: String,
    val image: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(bookmark: Bookmark): BookmarkResponse = BookmarkResponse(
            id = bookmark.id,
            url = bookmark.url,
            title = bookmark.title,
            description = bookmark.description,
            image = bookmark.image,
            createdAt = bookmark.createdAt
        )
    }
}