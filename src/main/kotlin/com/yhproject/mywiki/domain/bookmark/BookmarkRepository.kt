package com.yhproject.mywiki.domain.bookmark

interface BookmarkRepository {
    fun save(bookmark: Bookmark): Bookmark

    fun findById(id: Long): Bookmark?

    fun findByUserId(userId: Long, cursor: Long?, size: Int): List<Bookmark>

    fun findRandomByUserId(userId: Long): Bookmark?

    fun delete(bookmarkId: Long)
}