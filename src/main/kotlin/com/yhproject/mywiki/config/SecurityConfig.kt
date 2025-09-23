package com.yhproject.mywiki.config

import com.yhproject.mywiki.auth.CustomOAuth2UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customOAuth2UserService: CustomOAuth2UserService
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/error").permitAll()
                    .requestMatchers("/api/v1/**").hasRole("USER")
                    .anyRequest().authenticated()
            }
            .logout { logout -> logout.logoutSuccessUrl("/") }
            .oauth2Login { oauth2 ->
                oauth2.userInfoEndpoint { userInfo ->
                    userInfo.userService(customOAuth2UserService)
                }
            }

        return http.build()
    }
}
