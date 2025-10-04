package com.yhproject.mywiki.dto

import com.yhproject.mywiki.domain.summary.Summary


data class SummaryCreateRequest(
    val bookmarkId: Long,
    val content: String,
)

data class UpdateSummaryRequest(
    val content: String,
)

data class SummariesResponse(
    val summaries: List<SummaryResponse>
)

data class SummaryResponse(
    val id: Long,
    val bookmarkId: Long,
    val content: String,
    val createdAt: String
) {
    companion object {
        fun from(summary: Summary): SummaryResponse {
            return SummaryResponse(
                id = summary.id,
                bookmarkId = summary.bookmark.id,
                content = summary.content,
                createdAt = summary.createdAt.toString()
            )
        }
    }
}
