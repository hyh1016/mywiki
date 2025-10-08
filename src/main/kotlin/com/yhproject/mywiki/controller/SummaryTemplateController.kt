package com.yhproject.mywiki.controller

import com.yhproject.mywiki.dto.SummaryTemplateResponse
import com.yhproject.mywiki.service.SummaryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/summary-templates")
class SummaryTemplateController(
    private val summaryService: SummaryService
) {

    @GetMapping
    fun getSummaryTemplates(): ResponseEntity<SummaryTemplateResponse> {
        val templates = summaryService.getSummaryTemplates()
        return ResponseEntity.ok(SummaryTemplateResponse.from(templates))
    }
}
