package com.yhproject.mywiki.auth

import com.yhproject.mywiki.domain.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

class PrincipalDetails(
    val user: User,
    private val attributes: Map<String, Any>,
    private val nameAttributeKey: String
) : UserDetails, OAuth2User {

    val sessionUser: SessionUser = SessionUser(user)

    // OAuth2User methods
    override fun getName(): String {
        return attributes[nameAttributeKey].toString()
    }

    override fun getAttributes(): Map<String, Any> {
        return attributes
    }

    // UserDetails methods
    override fun getPassword(): String? {
        return null
    }

    override fun getUsername(): String {
        return user.email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    // Common method for both interfaces
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return setOf(SimpleGrantedAuthority(user.role.key))
    }
}
