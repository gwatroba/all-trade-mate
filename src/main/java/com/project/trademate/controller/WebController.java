package com.project.trademate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    @GetMapping("/")
    public ResponseEntity<String> getHomePage() {
        return ResponseEntity.status(200).body("Hello on main page");
    }
}
