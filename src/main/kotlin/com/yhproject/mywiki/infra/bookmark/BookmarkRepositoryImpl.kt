package com.yhproject.mywiki.infra.bookmark

import com.yhproject.mywiki.domain.bookmark.Bookmark
import com.yhproject.mywiki.domain.bookmark.BookmarkRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

@Repository
class BookmarkRepositoryImpl(
    private val jpaRepository: BookmarkJpaRepository
) : BookmarkRepository {

    override fun save(bookmark: Bookmark): Bookmark {
        return jpaRepository.save(bookmark)
    }

    override fun findById(id: Long): Bookmark? {
        return jpaRepository.findById(id).orElse(null)
    }

    override fun findByIdAndUserId(id: Long, userId: Long): Bookmark? {
        return jpaRepository.findByIdAndUserId(id, userId)
    }

    override fun findByUserId(userId: Long, cursor: Long?, size: Int): List<Bookmark> {
        val pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"))

        return if (cursor == null) {
            jpaRepository.findByUserIdOrderByIdDesc(userId, pageable)
        } else {
            jpaRepository.findByUserIdAndIdLessThanOrderByIdDesc(userId, cursor, pageable)
        }
    }

    override fun findRandomByUserId(userId: Long): Bookmark? {
        return jpaRepository.findRandomByUserId(userId)
    }

    override fun delete(bookmarkId: Long) {
        jpaRepository.deleteById(bookmarkId)
    }
}
