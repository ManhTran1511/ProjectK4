package com.fpt.edu.controllers;

import com.fpt.edu.models.Ban;
import com.fpt.edu.models.Category;
import com.fpt.edu.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "admin/cate")
public class CategoryController {
    @Autowired
    CategoryRepository categoryRepository;

    // Danh sach Categories
    @ModelAttribute("allCates")
    public List<Category> categoryList(){
        return categoryRepository.findAll();
    }

    @RequestMapping(value = "")
    public String cateManager(Model model) {
        return "admin_templates/cate_index";
    }

    @RequestMapping(value = "/new")
    public String addForm(Model model) {
        Category cate = new Category();
        model.addAttribute("cate", cate);
        return "admin_templates/cate_add_form";
    }

    @RequestMapping(value = "/add")
    public String add(@Valid Category cate,
                      BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin_templates/cate_add_form";
        }
        categoryRepository.save(cate);
        return "redirect:/admin/cate";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Category cate = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Category Id:" + id));

        model.addAttribute("cate", cate);
        model.addAttribute("allCates", categoryRepository.findAll());
        return "admin_templates/cate_edit_form";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") long id, @Valid Category cate,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            cate.setId(id);
            return "admin_templates/cate_edit_form";
        }

        categoryRepository.save(cate);
        return "redirect:/admin/cate";
    }

    @GetMapping("/delete/{id}")
    public String deleteTable(@PathVariable("id") long id, Model model) {
        Category cate = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Category Id:" + id));
        categoryRepository.delete(cate);
        return "redirect:/admin/cate";
    }
}
