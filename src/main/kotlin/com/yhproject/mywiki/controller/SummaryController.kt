package com.yhproject.mywiki.controller

import com.yhproject.mywiki.auth.LoginUser
import com.yhproject.mywiki.auth.SessionUser
import com.yhproject.mywiki.dto.SummariesResponse
import com.yhproject.mywiki.dto.SummaryCreateRequest
import com.yhproject.mywiki.dto.SummaryResponse
import com.yhproject.mywiki.dto.UpdateSummaryRequest
import com.yhproject.mywiki.service.SummaryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/summaries")
class SummaryController(
    private val summaryService: SummaryService
) {

    @PostMapping
    fun createSummary(
        @RequestBody request: SummaryCreateRequest,
        @LoginUser user: SessionUser
    ): ResponseEntity<SummaryResponse> {
        val summary = summaryService.createSummary(request, user.id)
        val templates = summaryService.getSummaryTemplates()
        val response = SummaryResponse.from(summary, templates)
        return ResponseEntity
            .created(URI.create("/api/summaries/" + summary.id))
            .body(response)
    }

    @PutMapping("/{summaryId}")
    fun updateSummary(
        @PathVariable summaryId: Long,
        @RequestBody request: UpdateSummaryRequest,
        @LoginUser user: SessionUser
    ): ResponseEntity<SummaryResponse> {
        val summary = summaryService.updateSummary(summaryId, request, user.id)
        val templates = summaryService.getSummaryTemplates()
        val response = SummaryResponse.from(summary, templates)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{summaryId}")
    fun getSummary(
        @PathVariable("summaryId") summaryId: Long,
        @LoginUser user: SessionUser
    ): ResponseEntity<SummaryResponse> {
        val summary = summaryService.getSummary(summaryId, user.id)
        val templates = summaryService.getSummaryTemplates()
        val response = SummaryResponse.from(summary, templates)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getSummariesByUser(
        @LoginUser user: SessionUser
    ): ResponseEntity<SummariesResponse> {
        val summaries = summaryService.getSummariesByUserId(user.id)
        return ResponseEntity.ok(SummariesResponse.from(summaries))
    }

    @GetMapping(params = ["bookmarkId"])
    fun getSummaryByBookmark(
        @RequestParam bookmarkId: Long,
        @LoginUser user: SessionUser
    ): ResponseEntity<SummaryResponse> {
        val summary = summaryService.getSummaryByBookmarkId(bookmarkId, user.id)
        val templates = summaryService.getSummaryTemplates()
        val response = SummaryResponse.from(summary, templates)
        return ResponseEntity.ok(response)
    }
}