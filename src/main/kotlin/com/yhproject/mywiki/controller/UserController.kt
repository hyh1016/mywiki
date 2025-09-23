package com.yhproject.mywiki.controller

import com.yhproject.mywiki.auth.SessionUser
import jakarta.servlet.http.HttpSession
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val httpSession: HttpSession
) {

    @GetMapping("/me")
    fun getMyInfo(): SessionUser? {
        return httpSession.getAttribute("user") as? SessionUser
    }
}
