package com.yhproject.mywiki.infra.summary

import com.yhproject.mywiki.domain.summary.Summary
import com.yhproject.mywiki.domain.summary.SummaryRepository
import org.springframework.stereotype.Repository

@Repository
class SummaryRepositoryImpl(
    private val jpaRepository: SummaryJpaRepository
) : SummaryRepository {

    override fun save(summary: Summary): Summary {
        return jpaRepository.save(summary)
    }

    override fun findByIdWithBookmark(summaryId: Long): Summary? {
        return jpaRepository.findByIdWithBookmark(summaryId).orElse(null)
    }

    override fun findAllByUserId(userId: Long): List<Summary> {
        return jpaRepository.findAllByBookmarkUserIdOrderByCreatedAtDesc(userId)
    }

    override fun findByBookmarkIdWithBookmark(bookmarkId: Long): Summary? {
        return jpaRepository.findByBookmarkIdWithBookmark(bookmarkId).orElse(null)
    }

    override fun existsByBookmarkId(bookmarkId: Long): Boolean {
        return jpaRepository.existsByBookmarkId(bookmarkId)
    }

    override fun deleteByBookmarkId(bookmarkId: Long) {
        return jpaRepository.deleteAllByBookmarkId(bookmarkId)
    }
}
