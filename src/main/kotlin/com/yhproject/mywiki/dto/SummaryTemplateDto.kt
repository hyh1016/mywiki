package com.yhproject.mywiki.dto

import com.yhproject.mywiki.domain.summary.SummaryTemplate
import com.yhproject.mywiki.domain.summary.SummaryTemplateSection

data class SummaryTemplateResponse(
    val templates: Map<String, SummaryTemplateSectionDto>
) {
    companion object {
        fun from(summaryTemplates: List<SummaryTemplate>): SummaryTemplateResponse {
            val templatesMap = summaryTemplates
                .groupBy { it.section }
                .map { (section, templates) ->
                    section.title to SummaryTemplateSectionDto.from(section, templates)
                }
                .toMap()
            return SummaryTemplateResponse(templatesMap)
        }
    }
}

data class SummaryTemplateSectionDto(
    val order: Int,
    val type: String,
    val element: List<SummaryTemplate>
) {
    companion object {
        fun from(section: SummaryTemplateSection, templates: List<SummaryTemplate>): SummaryTemplateSectionDto {
            return SummaryTemplateSectionDto(
                order = section.section,
                type = section.type.name,
                element = templates
            )
        }
    }
}
