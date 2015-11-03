package com.github.toastshaman.example.oauth.resources;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginResource {

    @RequestMapping("/login")
    public String loginPage() {
        return "login";
    }
}
