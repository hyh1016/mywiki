package com.yhproject.mywiki.auth

import com.yhproject.mywiki.domain.user.User
import java.io.Serializable

data class SessionUser(
    val name: String,
    val email: String
) : Serializable {
    constructor(user: User) : this(user.name, user.email)
}
