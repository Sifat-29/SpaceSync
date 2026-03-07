package com.spacesync.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // This catches requests to the root URL "/"
    @GetMapping("/")
    public String home() {
        // This tells Spring Boot to render the "index.html" file in the templates folder
        return "index";
    }
}
