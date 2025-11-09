package com.yhproject.mywiki.infra.summary

import com.yhproject.mywiki.domain.summary.Summary
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface SummaryJpaRepository : JpaRepository<Summary, Long> {

    @Query("SELECT s FROM Summary s JOIN FETCH s.bookmark b WHERE b.userId = :userId ORDER BY s.createdAt DESC")
    fun findAllByBookmarkUserIdOrderByCreatedAtDesc(userId: Long): List<Summary>

    @Query("SELECT s FROM Summary s JOIN FETCH s.bookmark WHERE s.id = :summaryId")
    fun findByIdWithBookmark(summaryId: Long): Optional<Summary>

    @Query("SELECT s FROM Summary s JOIN FETCH s.bookmark WHERE s.bookmark.id = :bookmarkId")
    fun findByBookmarkIdWithBookmark(bookmarkId: Long): Optional<Summary>

    fun existsByBookmarkId(bookmarkId: Long): Boolean

    fun deleteAllByBookmarkId(bookmarkId: Long)
}
