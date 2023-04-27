package com.fpt.edu.controllers;

import com.fpt.edu.models.Blog;
import com.fpt.edu.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@Controller
@RequestMapping(value = "admin/blog")

public class BlogController {
    @Autowired
    BlogRepository blogRepository;

    @ModelAttribute("allBlogs")
    public List<Blog> blogList() {
        return blogRepository.findAll();
    }


    @RequestMapping(value = "")
    public String blogManager(Model model) {
        return "admin_templates/blog_index";
    }

    @RequestMapping(value = "/new")
    public String addForm(Model model) {
        Blog blog = new Blog();
        model.addAttribute("blog", blog);
        return "admin_templates/blog_add_form";
    }

    @RequestMapping(value = "/add")
    public String add(@Valid Blog blog,
                      BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin_templates/blog_add_form";
        }
        blogRepository.save(blog);
        return "redirect:/admin/blog";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Blog Id:" + id));

        model.addAttribute("blog", blog);
        model.addAttribute("allBlogs", blogRepository.findAll());
        return "admin_templates/blog_edit_form";
    }

    @PostMapping("/update/{id}")
    public String updateBlog(@PathVariable("id") long id, @Valid Blog blog,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            blog.setId(id);
            return "admin_templates/blog_edit_form";
        }

        blogRepository.save(blog);
        return "redirect:/admin/blog";
    }

    @GetMapping("/delete/{id}")
    public String deleteBlog(@PathVariable("id") long id, Model model) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Blog Id:" + id));
        blogRepository.delete(blog);
        return "redirect:/admin/blog";
    }

}
