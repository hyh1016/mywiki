package com.yhproject.mywiki.domain.user

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var role: Role,

    var provider: String,

    var providerId: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {

    fun update(name: String): User {
        this.name = name
        this.updatedAt = LocalDateTime.now()
        return this
    }
}

enum class Role(val key: String, val title: String) {
    GUEST("ROLE_GUEST", "비로그인 사용자"),
    USER("ROLE_USER", "로그인 사용자")
}
