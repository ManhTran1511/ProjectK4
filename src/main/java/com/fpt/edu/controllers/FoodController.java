package com.fpt.edu.controllers;

import com.fpt.edu.models.Category;
import com.fpt.edu.models.Food;
import com.fpt.edu.repository.CategoryRepository;
import com.fpt.edu.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "admin/food")
public class FoodController {
    @Autowired
    FoodRepository foodRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @ModelAttribute("allFoods")
    public List<Food> foodList() {
        return foodRepository.findAll();
    }

    @RequestMapping(value = "")
    public String foodManager(Model model) {
        model.addAttribute("allCates", categoryRepository.findAll());
        return "admin_templates/food_index";
    }

    @RequestMapping(value = "/new")
    public String addForm(Model model) {
        Food food = new Food();
        model.addAttribute("food", food);
        model.addAttribute("allCates", categoryRepository.findAll());
        return "admin_templates/food_add_form";
    }

    @RequestMapping(value = "/add")
    public String add(@Valid Food food,
                      BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin_templates/food_add_form";
        }
        foodRepository.save(food);
        return "redirect:/admin/food";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Food Id:" + id));

        model.addAttribute("food", food);
        model.addAttribute("allCates", categoryRepository.findAll());
        return "admin_templates/food_edit_form";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") long id, @Valid Food food,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            food.setId(id);
            return "admin_templates/food_edit_form";
        }

        foodRepository.save(food);
        return "redirect:/admin/food";
    }

    @GetMapping("/delete/{id}")
    public String deleteTable(@PathVariable("id") long id, Model model) {
        Food food = foodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Food Id:" + id));
        foodRepository.delete(food);
        return "redirect:/admin/food";
    }
}
