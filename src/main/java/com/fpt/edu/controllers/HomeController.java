package com.fpt.edu.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "")
public class HomeController {

    // USER ROUTES
    @GetMapping("home")
    public String home(Model model) {
        return "index";
    }

    @GetMapping("reservation")
    public String reservation(Model model) {
        return "user_templates/reservation";
    }

    @GetMapping("menu")
    public String menu(Model model) {
        return "user_templates/menu";
    }

    @GetMapping("gallery")
    public String gallery(Model model) {
        return "user_templates/gallery";
    }

    @GetMapping("about")
    public String about(Model model) {
        return "user_templates/about";
    }

    @GetMapping("blog")
    public String blog(Model model) {
        return "user_templates/blog";
    }

    @GetMapping("blog_detail")
    public String blog_detail(Model model) {
        return "user_templates/blog_detail";
    }

    @GetMapping("contact")
    public String contact(Model model) {
        return "user_templates/contact";
    }

    // ADMIN ROUTES
    @GetMapping("admin")
    public String admin(Model model) {
        return "admin_templates/index";
    }

    @GetMapping("ban")
    public String ban(Model model) {
        return "admin_templates/table_index";
    }

    @GetMapping("cate")
    public String cate(Model model) {
        return "admin_templates/cate_index";
    }

    @GetMapping("food")
    public String food(Model model) {
        return "admin_templates/food_index";
    }
    @GetMapping("table_add_form")
    public String table_add_form(Model model) {
        return "admin_templates/table_add_form";
    }

    @GetMapping("staff")
    public String staff(Model model) {
        return "admin_templates/staff";
    }

    @GetMapping("booking")
    public String booking(Model model) {
        return "admin_templates/booking_index";
    }

//    @GetMapping("staff/booking")
//    public String booking(Model model) {
//        return "admin_templates/booking_add_form";
//    }
}
