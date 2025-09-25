package com.yhproject.mywiki.domain.bookmark

import org.springframework.data.jpa.repository.JpaRepository

interface BookmarkRepository : JpaRepository<Bookmark, Long> {
    fun findAllByUserId(userId: Long): List<Bookmark>
}
