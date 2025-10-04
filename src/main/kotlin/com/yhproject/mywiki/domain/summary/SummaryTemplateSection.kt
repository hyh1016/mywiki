package com.yhproject.mywiki.domain.summary

enum class SummaryTemplateSection(
    val section: Int,
    val title: String,
    val type: SummaryTemplateSectionType
) {

    BIG_PICTURE(1, "핵심 파악", SummaryTemplateSectionType.STATIC),
    DETAILS(2, "세부 내용 정리", SummaryTemplateSectionType.SELECT),
    EXPANSION(3, "사고 확장", SummaryTemplateSectionType.MULTI_SELECT);
}

enum class SummaryTemplateSectionType {

    // 고정된 단 한 개의 템플릿
    STATIC,
    // 여러 템플릿 중 한 가지를 선택
    SELECT,
    // 여러 템플릿 중 0~N 가지를 선택
    MULTI_SELECT
}
