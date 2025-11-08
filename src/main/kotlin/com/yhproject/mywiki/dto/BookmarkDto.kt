package com.yhproject.mywiki.dto

import com.yhproject.mywiki.domain.bookmark.Bookmark
import java.time.LocalDateTime

data class BookmarkCreateRequest(
    val url: String
)

data class BookmarkCursorResponse(
    val content: List<BookmarkResponse>,
    val nextCursor: Long?
) {
    companion object {
        fun from(bookmarkSlice: BookmarkSlice): BookmarkCursorResponse {
            return BookmarkCursorResponse(
                bookmarkSlice.content.map { bookmark -> BookmarkResponse.from(bookmark) },
                bookmarkSlice.nextCursor
            )
        }
    }
}

data class BookmarkSlice(
    val content: List<Bookmark>,
    val nextCursor: Long?
)

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