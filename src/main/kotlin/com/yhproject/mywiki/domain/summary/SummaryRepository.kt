package com.yhproject.mywiki.domain.summary

import com.yhproject.mywiki.domain.bookmark.Bookmark
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface SummaryRepository : JpaRepository<Summary, Long> {
    fun findByBookmark(bookmark: Bookmark): Optional<Summary>

    @Query("SELECT s FROM Summary s JOIN FETCH s.bookmark b WHERE b.userId = :userId ORDER BY s.createdAt DESC")
    fun findAllByBookmarkUserIdOrderByCreatedAtDesc(userId: Long): List<Summary>

    @Query("SELECT s FROM Summary s JOIN FETCH s.bookmark WHERE s.id = :summaryId")
    fun findByIdWithBookmark(summaryId: Long): Optional<Summary>

    @Query("SELECT s FROM Summary s JOIN FETCH s.bookmark WHERE s.bookmark.id = :bookmarkId")
    fun findByBookmarkIdWithBookmark(bookmarkId: Long): Optional<Summary>
}
