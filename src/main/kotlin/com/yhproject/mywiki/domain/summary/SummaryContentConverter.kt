package com.yhproject.mywiki.domain.summary

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Converter(autoApply = false)
@Component
class SummaryContentConverter(
    private val objectMapper: ObjectMapper
) : AttributeConverter<SummaryContents, String> {

    override fun convertToDatabaseColumn(attribute: SummaryContents?): String? {
        return attribute?.let { objectMapper.writeValueAsString(it) }
    }

    override fun convertToEntityAttribute(dbData: String?): SummaryContents? {
        return dbData?.let {
            objectMapper.readValue(it, SummaryContents::class.java)
        }
    }
}