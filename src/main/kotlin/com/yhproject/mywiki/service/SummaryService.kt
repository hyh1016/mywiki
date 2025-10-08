package com.yhproject.mywiki.service

import com.yhproject.mywiki.domain.bookmark.Bookmark
import com.yhproject.mywiki.domain.bookmark.BookmarkRepository
import com.yhproject.mywiki.domain.summary.Summary
import com.yhproject.mywiki.domain.summary.SummaryRepository
import com.yhproject.mywiki.domain.summary.SummaryTemplate
import com.yhproject.mywiki.domain.summary.SummaryTemplateRepository
import com.yhproject.mywiki.dto.SummaryCreateRequest
import com.yhproject.mywiki.dto.UpdateSummaryRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SummaryService(
    private val summaryRepository: SummaryRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val summaryTemplateRepository: SummaryTemplateRepository
) {

    @Transactional
    fun createSummary(request: SummaryCreateRequest, userId: Long): Summary {
        val bookmark = getBookmark(request.bookmarkId)
        if (bookmark.userId != userId) {
            throw IllegalAccessException("User does not have permission for this bookmark: ${request.bookmarkId}")
        }

        val summary = Summary(
            bookmark = bookmark,
            content = request.content,
        )
        val id = summaryRepository.save(summary).id
        return summaryRepository.findByIdWithBookmark(id).get()
    }

    @Transactional
    fun updateSummary(summaryId: Long, request: UpdateSummaryRequest, userId: Long): Summary {
        val summary = summaryRepository.findByIdWithBookmark(summaryId)
            .orElseThrow { IllegalArgumentException("Summary not found with id: $summaryId") }

        if (summary.bookmark.userId != userId) {
            throw IllegalAccessException("User does not have permission for this summary")
        }

        summary.content = request.content
        summary.updatedAt = java.time.LocalDateTime.now()

        return summaryRepository.save(summary)
    }

    @Transactional(readOnly = true)
    fun getSummariesByUserId(userId: Long): List<Summary> {
        return summaryRepository.findAllByBookmarkUserIdOrderByCreatedAtDesc(userId)
    }

    @Transactional(readOnly = true)
    fun getSummaryByBookmarkId(bookmarkId: Long, userId: Long): Summary {
        val summary = summaryRepository.findByBookmarkIdWithBookmark(bookmarkId)
            .orElseThrow { IllegalArgumentException("Summary not found for bookmark with id: $bookmarkId") }

        if (summary.bookmark.userId != userId) {
            throw IllegalAccessException("User $userId does not have permission for this bookmark $bookmarkId")
        }

        return summary
    }

    @Transactional(readOnly = true)
    fun getSummaryTemplates(): List<SummaryTemplate> {
        return summaryTemplateRepository.findAll()
    }

    private fun getBookmark(bookmarkId: Long): Bookmark {
        return bookmarkRepository.findByIdOrNull(bookmarkId)
            ?: throw IllegalArgumentException("Bookmark not found with id: $bookmarkId")
    }
}
