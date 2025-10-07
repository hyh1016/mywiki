package com.yhproject.mywiki.service

import com.yhproject.mywiki.domain.bookmark.Bookmark
import com.yhproject.mywiki.domain.bookmark.BookmarkRepository
import com.yhproject.mywiki.domain.summary.Summary
import com.yhproject.mywiki.domain.summary.SummaryRepository
import com.yhproject.mywiki.domain.summary.SummaryTemplateRepository
import com.yhproject.mywiki.dto.*
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
    fun createSummary(request: SummaryCreateRequest, userId: Long): SummaryResponse {
        val bookmark = getBookmark(request.bookmarkId)
        if (bookmark.userId != userId) {
            throw IllegalAccessException("User does not have permission for this bookmark: ${request.bookmarkId}")
        }

        val summary = Summary(
            bookmark = bookmark,
            content = request.content,
        )
        val savedSummary = summaryRepository.save(summary)
        return SummaryResponse.from(savedSummary)
    }

    @Transactional
    fun updateSummary(summaryId: Long, request: UpdateSummaryRequest, userId: Long): SummaryResponse {
        val summary = summaryRepository.findByIdOrNull(summaryId)
            ?: throw IllegalArgumentException("Summary not found with id: $summaryId")

        if (summary.bookmark.userId != userId) {
            throw IllegalAccessException("User does not have permission for this summary")
        }

        summary.content = request.content
        summary.updatedAt = java.time.LocalDateTime.now()

        val updatedSummary = summaryRepository.save(summary)
        return SummaryResponse.from(updatedSummary)
    }

    @Transactional(readOnly = true)
    fun getSummariesByUserId(userId: Long): SummariesResponse {
        val summaries = summaryRepository.findAllByBookmarkUserIdOrderByCreatedAtDesc(userId)
        val summaryResponses = summaries.map { SummaryResponse.from(it) }
        return SummariesResponse(summaryResponses)
    }

    @Transactional(readOnly = true)
    fun getSummaryByBookmarkId(bookmarkId: Long, userId: Long): SummaryResponse {
        val bookmark = getBookmark(bookmarkId)
        if (bookmark.userId != userId) {
            throw IllegalAccessException("User $userId does not have permission for this bookmark $bookmarkId")
        }

        val summary = summaryRepository.findByBookmark(bookmark)
            .orElseThrow { IllegalArgumentException("Summary not found for bookmark with id: $bookmarkId") }
        return SummaryResponse.from(summary)
    }

    @Transactional(readOnly = true)
    fun getSummaryTemplates(): SummaryTemplateResponse {
        val summaryTemplates = summaryTemplateRepository.findAll()
        return SummaryTemplateResponse.from(summaryTemplates)
    }

    private fun getBookmark(bookmarkId: Long): Bookmark {
        return bookmarkRepository.findByIdOrNull(bookmarkId)
            ?: throw IllegalArgumentException("Bookmark not found with id: $bookmarkId")
    }
}
