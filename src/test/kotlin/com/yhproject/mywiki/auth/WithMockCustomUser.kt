package com.yhproject.mywiki.auth

import com.yhproject.mywiki.domain.user.Role
import com.yhproject.mywiki.domain.user.User
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContext
import org.springframework.security.test.context.support.WithSecurityContextFactory

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory::class)
annotation class WithMockCustomUser(
    val role: String = "USER"
)

class WithMockCustomUserSecurityContextFactory : WithSecurityContextFactory<WithMockCustomUser> {
    override fun createSecurityContext(annotation: WithMockCustomUser): SecurityContext {
        val context = SecurityContextHolder.createEmptyContext()

        val id = 1L
        val name = "testuser"

        val user = User(
            id,
            name,
            email = "test@email.com",
            role = Role.valueOf(annotation.role),
            provider = "google",
            providerId = "12345"
        )
        val principal = PrincipalDetails(user = user, emptyMap(), "sub")

        val auth = UsernamePasswordAuthenticationToken(principal, "", principal.authorities)
        context.authentication = auth
        return context
    }
}
