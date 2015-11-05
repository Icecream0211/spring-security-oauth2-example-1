package com.github.toastshaman.example.oauth.resources;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserResource {

    @RequestMapping("/me")
    public Principal user(Principal user) {
        return user;
    }
}
