package com.yhproject.mywiki.config

import com.yhproject.mywiki.auth.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customOAuth2UserService: CustomOAuth2UserService,
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .headers { it.frameOptions { frameOptions -> frameOptions.disable() } }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/error").permitAll()
                    .requestMatchers("/api/**").hasRole("USER")
                    .anyRequest().authenticated()
            }
            .logout { it.logoutSuccessUrl("/") }
            .oauth2Login {
                it.userInfoEndpoint {
                    it.userService(customOAuth2UserService)
                }
            }
            .exceptionHandling { handler ->
                handler.defaultAuthenticationEntryPointFor(
                    { _, response, _ ->
                        response.status = HttpStatus.UNAUTHORIZED.value()
                    },
                    { request ->
                        request.requestURI.startsWith("/api/")
                    }
                )
            }

        return http.build()
    }
}
