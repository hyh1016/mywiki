package com.yhproject.mywiki.domain.summary

import com.yhproject.mywiki.domain.bookmark.Bookmark
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SummaryRepository : JpaRepository<Summary, Long> {
    fun findByBookmark(bookmark: Bookmark): Optional<Summary>

    fun findAllByBookmarkUserIdOrderByCreatedAtDesc(userId: Long): List<Summary>
}
