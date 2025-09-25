package com.yhproject.mywiki.auth

import com.yhproject.mywiki.domain.user.User
import java.io.Serializable

data class SessionUser(
    val id: Long,
    val name: String
) : Serializable {
    constructor(user: User) : this(user.id, user.name)
}
