package com.yhproject.mywiki.controller

import com.yhproject.mywiki.auth.LoginUser
import com.yhproject.mywiki.auth.SessionUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController {

    @GetMapping("/me")
    fun getMyInfo(@LoginUser user: SessionUser): SessionUser {
        return user
    }
}
