package com.fpt.edu.controllers;

import com.fpt.edu.models.Blog;
import com.fpt.edu.models.Contact;
import com.fpt.edu.models.Contact_check;
import com.fpt.edu.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "")
public class HomeController {

    @Autowired
    FoodRepository foodRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ChefRepository chefRepository;

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    Contact_checkRepository contact_checkRepository;

    @Autowired
    BlogRepository blogRepository;

    // USER ROUTES
    @GetMapping("home")
    public String home(Model model) {
        List<Blog> list3Blogs = new ArrayList<>();
        int count = 0;

        for(Blog blog : blogRepository.findAll()){
            if (count < 3){
                list3Blogs.add(blog);
                count = count + 1;
            } else {
                break;
            }
        }

        model.addAttribute("list3Blogs", blogRepository.findAll());
        model.addAttribute("listContact", contactRepository.findAll());        return "index";
    }

    @GetMapping("reservation")
    public String reservation(Model model) {
        return "user_templates/reservation";
    }

    @GetMapping("menu")
    public String menu(Model model) {
        model.addAttribute("listFood", foodRepository.findAll());
        model.addAttribute("listCate", categoryRepository.findAll());
        return "user_templates/menu";
    }

    @GetMapping("gallery")
    public String gallery(Model model) {
        return "user_templates/gallery";
    }

    @GetMapping("about")
    public String about(Model model) {
        model.addAttribute("listChef", chefRepository.findAll());
        return "user_templates/about";
    }

    @GetMapping("blog")
    public String blog(Model model) {
        model.addAttribute("listBlog", blogRepository.findAll());
        return "user_templates/blog";
    }

    @GetMapping("blog_detail")
    public String blog_detail(Model model) {
        return "user_templates/blog_detail";
    }

    @GetMapping("contact")
    public String contact(Model model) {
        Contact_check contact_check = new Contact_check();
        model.addAttribute("contact_check", contact_check);
        model.addAttribute("listContact",contactRepository.findAll());
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
