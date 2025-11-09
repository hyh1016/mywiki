package com.yhproject.mywiki.infra.bookmark

import com.yhproject.mywiki.domain.bookmark.Bookmark
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BookmarkJpaRepository : JpaRepository<Bookmark, Long> {

    fun findByIdAndUserId(id: Long, userId: Long): Bookmark?

    fun findByUserIdOrderByIdDesc(userId: Long, pageable: Pageable): List<Bookmark>

    fun findByUserIdAndIdLessThanOrderByIdDesc(userId: Long, id: Long, pageable: Pageable): List<Bookmark>

    @Query(value = "SELECT * FROM bookmarks WHERE user_id = :userId ORDER BY RAND() LIMIT 1", nativeQuery = true)
    fun findRandomByUserId(@Param("userId") userId: Long): Bookmark?
}
