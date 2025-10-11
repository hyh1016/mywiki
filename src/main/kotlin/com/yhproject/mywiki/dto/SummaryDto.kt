package com.yhproject.mywiki.dto

import com.yhproject.mywiki.domain.summary.Summary
import com.yhproject.mywiki.domain.summary.SummaryContentItem
import com.yhproject.mywiki.domain.summary.SummaryTemplate
import java.time.LocalDateTime

// --- Request DTOs ---
data class SummaryCreateRequest(
    val bookmarkId: Long,
    val contents: List<SummaryContentItem>
)

data class UpdateSummaryRequest(
    val contents: List<SummaryContentItem>
)


// --- Response DTOs ---

// For single, detailed summary response
data class SummaryResponse(
    val id: Long,
    val bookmark: BookmarkResponse,
    val contents: SummaryDetailResponse,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(summary: Summary, allTemplates: List<SummaryTemplate>): SummaryResponse {
            val userContents = summary.contents.contents.associateBy { it.id }

            val groupedBySection = allTemplates
                .filter { userContents.containsKey(it.id) }
                .groupBy { it.section }
                .mapValues { (section, templates) ->
                    SummaryDetailSection(
                        title = section.title,
                        content = templates.map { template ->
                            SummaryDetailContentItem(
                                id = template.id,
                                title = template.title,
                                description = template.description,
                                content = userContents[template.id]?.content ?: ""
                            )
                        }
                    )
                }.mapKeys { it.key.name }

            return SummaryResponse(
                id = summary.id,
                bookmark = BookmarkResponse.from(summary.bookmark),
                contents = groupedBySection,
                createdAt = summary.createdAt,
                updatedAt = summary.updatedAt,
            )
        }
    }
}

data class SummaryDetailContentItem(
    val id: Long,
    val title: String,
    val description: String?,
    val content: String
)

data class SummaryDetailSection(
    val title: String,
    val content: List<SummaryDetailContentItem>
)

typealias SummaryDetailResponse = Map<String, SummaryDetailSection>


// For list response
data class SummariesResponse(
    val summaries: List<SummaryListResponse>
) {
    companion object {
        fun from(summaries: List<Summary>): SummariesResponse {
            return SummariesResponse(summaries.map { summary -> SummaryListResponse.from(summary) })
        }
    }
}

data class SummaryListResponse(
    val id: Long,
    val bookmark: BookmarkResponse,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(summary: Summary): SummaryListResponse {
            return SummaryListResponse(
                id = summary.id,
                bookmark = BookmarkResponse.from(summary.bookmark),
                createdAt = summary.createdAt,
                updatedAt = summary.updatedAt,
            )
        }
    }
}
