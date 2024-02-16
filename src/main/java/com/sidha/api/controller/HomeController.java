package com.sidha.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class HomeController {

    @GetMapping
    public String home() {
        return "Welcome to SiDHA Services";
    }
}
