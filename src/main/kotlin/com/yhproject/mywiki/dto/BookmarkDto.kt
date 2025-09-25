package com.yhproject.mywiki.dto

import com.yhproject.mywiki.domain.bookmark.Bookmark

data class BookmarkCreateRequest(
    val url: String
)

data class BookmarkResponse(
    val id: Long,
    val url: String,
    val title: String,
    val description: String,
    val image: String
) {
    companion object {
        fun from(bookmark: Bookmark): BookmarkResponse = BookmarkResponse(
            id = bookmark.id,
            url = bookmark.url,
            title = bookmark.title,
            description = bookmark.description,
            image = bookmark.image
        )
    }
}