package com.yhproject.mywiki.domain.bookmark

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BookmarkRepository : JpaRepository<Bookmark, Long> {
    fun findAllByUserId(userId: Long): List<Bookmark>

    @Query(value = "SELECT * FROM bookmarks WHERE user_id = :userId ORDER BY RAND() LIMIT 1", nativeQuery = true)
    fun findRandomByUserId(@Param("userId") userId: Long): Bookmark?
}
