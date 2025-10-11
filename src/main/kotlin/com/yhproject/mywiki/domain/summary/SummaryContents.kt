package com.yhproject.mywiki.domain.summary

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

data class SummaryContents @JsonCreator constructor(
    @get:JsonValue
    val contents: List<SummaryContentItem>
)

data class SummaryContentItem(
    val id: Long,
    val content: String
)