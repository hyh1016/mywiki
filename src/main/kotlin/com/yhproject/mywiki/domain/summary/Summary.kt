package com.yhproject.mywiki.domain.summary

import com.yhproject.mywiki.domain.bookmark.Bookmark
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "summaries")
class Summary(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookmark_id", nullable = false)
    val bookmark: Bookmark,

    @Convert(converter = SummaryContentConverter::class)
    @Column(nullable = false, columnDefinition = "TEXT")
    var contents: SummaryContents,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
