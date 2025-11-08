package com.yhproject.mywiki.domain.summary

interface SummaryRepository {
    fun save(summary: Summary): Summary

    fun findByIdWithBookmark(summaryId: Long): Summary?

    fun findAllByUserId(userId: Long): List<Summary>

    fun findByBookmarkIdWithBookmark(bookmarkId: Long): Summary?
}