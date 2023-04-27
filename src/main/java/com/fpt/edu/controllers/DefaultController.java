package com.fpt.edu.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DefaultController {
    @GetMapping("/index")
    public String home(){
        return "index";
    }

    @RequestMapping("/default")
    public String defaultAfterLogin(HttpServletRequest request) {
        if (request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/admin";
        }
        if (request.isUserInRole("ROLE_STAFF")) {
            return "redirect:/staff";
        }
        return "redirect:/index";
    }
}

