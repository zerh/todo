package com.example.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {
    @GetMapping({"/", "/login"})
    public String login() {
        return "redirect:/auth";
    }
}
