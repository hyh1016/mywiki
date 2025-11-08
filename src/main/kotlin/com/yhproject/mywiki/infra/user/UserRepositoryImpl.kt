package com.yhproject.mywiki.infra.user

import com.yhproject.mywiki.domain.user.User
import com.yhproject.mywiki.domain.user.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val jpaRepository: UserJpaRepository
) : UserRepository {

    override fun findByEmail(email: String): User? {
        return jpaRepository.findByEmail(email).orElse(null)
    }

    override fun save(user: User): User {
        return jpaRepository.save(user)
    }

    override fun existsById(userId: Long): Boolean {
        return jpaRepository.existsById(userId)
    }
}
