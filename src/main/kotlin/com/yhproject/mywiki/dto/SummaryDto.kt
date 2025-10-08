package com.yhproject.mywiki.dto

import com.yhproject.mywiki.domain.summary.Summary
import java.time.LocalDateTime


data class SummaryCreateRequest(
    val bookmarkId: Long,
    val content: String,
)

data class UpdateSummaryRequest(
    val content: String,
)

data class SummariesResponse(
    val summaries: List<SummaryResponse>
) {
    companion object {
        fun from(summaries: List<Summary>): SummariesResponse {
            return SummariesResponse(summaries.map { summary -> SummaryResponse.from(summary) })
        }
    }
}

data class SummaryResponse(
    val id: Long,
    val bookmark: BookmarkResponse,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(summary: Summary): SummaryResponse {
            return SummaryResponse(
                id = summary.id,
                bookmark = BookmarkResponse.from(summary.bookmark),
                content = summary.content,
                createdAt = summary.createdAt,
                updatedAt = summary.updatedAt,
            )
        }
    }
}
