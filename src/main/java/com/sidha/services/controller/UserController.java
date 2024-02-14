package com.sidha.services.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @RequestMapping("/test")
    public String test() {
        return "Hello World!";
    }
}
