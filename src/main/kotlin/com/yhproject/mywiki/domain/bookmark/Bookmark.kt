package com.yhproject.mywiki.domain.bookmark

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "bookmarks")
class Bookmark(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val userId: Long = 0,

    @Column(nullable = false)
    var url: String,

    @Column(length = 511)
    val title: String,

    @Column(length = 1023)
    val description: String,

    @Column(length = 511)
    val image: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
