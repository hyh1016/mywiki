package com.yhproject.mywiki.config

import com.yhproject.mywiki.auth.CustomOAuth2UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customOAuth2UserService: CustomOAuth2UserService,
    @Value("\${app.oauth2.redirect-uri}") private val redirectUri: String
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .headers { it.frameOptions { frameOptions -> frameOptions.disable() } }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/error").permitAll()
                    .requestMatchers("/api/**").hasRole("USER")
                    .anyRequest().authenticated()
            }
            .logout { it.logoutSuccessUrl("/") }
            .oauth2Login { oauth2 ->
                oauth2.userInfoEndpoint { userInfo ->
                    userInfo.userService(customOAuth2UserService)
                }
                oauth2.successHandler(oauth2LoginSuccessHandler())
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

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:3000")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun oauth2LoginSuccessHandler(): AuthenticationSuccessHandler {
        return AuthenticationSuccessHandler { _, response, _ ->
            response.sendRedirect(redirectUri)
        }
    }
}