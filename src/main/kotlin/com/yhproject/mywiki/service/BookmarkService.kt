package com.yhproject.mywiki.service

import com.yhproject.mywiki.domain.bookmark.Bookmark
import com.yhproject.mywiki.domain.bookmark.BookmarkRepository
import com.yhproject.mywiki.domain.user.UserRepository
import com.yhproject.mywiki.dto.BookmarkCreateRequest
import com.yhproject.mywiki.dto.BookmarkSlice
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookmarkService(
    private val bookmarkRepository: BookmarkRepository,
    private val userRepository: UserRepository
) {
    private val logger = KotlinLogging.logger {}

    @Transactional
    fun createBookmark(request: BookmarkCreateRequest, userId: Long): Bookmark {
        validateUserExists(userId)

        // REFACTOR: 성능 이슈가 있으면 비동기로 전환
        val documentMetadata = getDocumentMetadata(request.url)

        val bookmark = Bookmark(
            userId = userId,
            url = request.url,
            title = documentMetadata.title,
            description = documentMetadata.description,
            image = documentMetadata.image
        )

        return bookmarkRepository.save(bookmark)
    }

    @Transactional(readOnly = true)
    fun getBookmarks(userId: Long, cursor: Long?, size: Int): BookmarkSlice {
        validateUserExists(userId)

        val bookmarks = bookmarkRepository.findByUserId(userId, cursor, size)

        val nextCursor = if (bookmarks.size == size) bookmarks.lastOrNull()?.id else null

        return BookmarkSlice(content = bookmarks, nextCursor = nextCursor)
    }

    @Transactional(readOnly = true)
    fun getBookmark(bookmarkId: Long, userId: Long): Bookmark {
        validateUserExists(userId)
        val bookmark = bookmarkRepository.findById(bookmarkId)
            ?: throw IllegalArgumentException("Bookmark not found: $bookmarkId")
        if (bookmark.userId != userId) {
            throw IllegalAccessException("User $userId does not have permission for this bookmark: $bookmarkId")
        }
        return bookmark
    }

    @Transactional(readOnly = true)
    fun getRandomBookmark(userId: Long): Bookmark {
        validateUserExists(userId)
        return bookmarkRepository.findRandomByUserId(userId)
            ?: throw NoSuchElementException("사용자의 북마크가 존재하지 않습니다. (userId: $userId)")
    }

    @Transactional
    fun deleteBookmark(bookmarkId: Long, userId: Long) {
        validateUserExists(userId)
        bookmarkRepository.delete(bookmarkId)
    }

    private fun validateUserExists(userId: Long) {
        if (!userRepository.existsById(userId)) {
            throw IllegalArgumentException("User not found with id: $userId")
        }
    }

    private fun getDocumentMetadata(url: String): DocumentMetadata {
        // url에 해당하는 html을 get 하여 metadata를 조회
        // title, description, image 메타데이터 획득
        try {
            val doc = Jsoup.connect(url)
                .timeout(5000) // 5초 타임아웃
                .get()

            // 1. Open Graph 태그 우선 조회
            val title = doc.select("meta[property=og:title]").attr("content").ifEmpty {
                // 2. og:title이 없으면 <title> 태그 조회
                doc.title()
            }
            val description = doc.select("meta[property=og:description]").attr("content").ifEmpty {
                // 3. og:description이 없으면 <meta name="description"> 조회
                doc.select("meta[name=description]").attr("content")
            }
            val image = doc.select("meta[property=og:image]").attr("content")

            return DocumentMetadata(
                // 만약 제목이 비어있다면 URL을 기본값으로 사용
                title = title.ifEmpty { url },
                description = description,
                image = image
            )
        } catch (e: Exception) {
            logger.error { "Error fetching metadata from url: $url, error: ${e.message}" }
            // 메타데이터 조회 실패 시, URL을 제목으로 하는 기본 데이터를 반환
            // REFACTOR: 일시적인 조회 실패라면? 나중에라도 데이터를 넣고 싶다면 어느 시점에 재시도할 것인가?
            return DocumentMetadata(title = url, description = "", image = "")
        }
    }

    data class DocumentMetadata(
        val title: String,
        val description: String,
        val image: String
    )
}
