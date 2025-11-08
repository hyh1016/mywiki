package com.yhproject.mywiki.domain.user

interface UserRepository {
    fun findByEmail(email: String): User?
    fun save(user: User): User
    fun existsById(userId: Long): Boolean
}