package com.yhproject.mywiki.infra.user

import com.yhproject.mywiki.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserJpaRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
}
