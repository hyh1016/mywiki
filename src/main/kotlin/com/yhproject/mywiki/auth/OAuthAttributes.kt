package com.yhproject.mywiki.auth

import com.yhproject.mywiki.domain.user.Role
import com.yhproject.mywiki.domain.user.User

data class OAuthAttributes(
    val attributes: Map<String, Any>,
    val nameAttributeKey: String,
    val name: String,
    val email: String,
    val provider: String,
    val providerId: String
) {
    fun toEntity(): User {
        return User(
            name = name,
            email = email,
            role = Role.USER,
            provider = provider,
            providerId = providerId
        )
    }

    companion object {
        fun of(registrationId: String, userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            return when (registrationId) {
                "google" -> ofGoogle(userNameAttributeName, attributes)
                else -> throw IllegalArgumentException("Unsupported provider: $registrationId")
            }
        }

        private fun ofGoogle(userNameAttributeName: String, attributes: Map<String, Any>): OAuthAttributes {
            return OAuthAttributes(
                name = attributes["name"] as String,
                email = attributes["email"] as String,
                provider = "google",
                providerId = attributes[userNameAttributeName] as String,
                attributes = attributes,
                nameAttributeKey = userNameAttributeName
            )
        }
    }
}
