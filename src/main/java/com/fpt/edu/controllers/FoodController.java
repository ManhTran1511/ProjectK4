package com.fpt.edu.controllers;

import com.fpt.edu.models.Category;
import com.fpt.edu.models.Food;
import com.fpt.edu.repository.CategoryRepository;
import com.fpt.edu.repository.FoodRepository;
import com.fpt.edu.security.storage.StorageFileNotFoundException;
import com.fpt.edu.security.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "admin/food")
public class FoodController {
    @Autowired
    FoodRepository foodRepository;

    @Autowired
    CategoryRepository categoryRepository;

    private final StorageService storageService;

    @Autowired
    public FoodController(StorageService storageService) {
        this.storageService = storageService;
    }


    @ModelAttribute("allFoods")
    public List<Food> foodList() {
        return foodRepository.findAll();
    }

    @RequestMapping(value = "")
    public String foodManager(Model model) throws IOException {
        storageService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(FoodController.class,
                                "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList());
        model.addAttribute("listFoods", foodRepository.findAll());
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

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @RequestMapping(value = "/add")
    public String add(@Valid Food food,
                      BindingResult result, Model model, @RequestParam("file") MultipartFile file) {
        if (result.hasErrors()) {
            return "admin_templates/food_add_form";
        }
        storageService.store(file);

        food.setImage(file.getOriginalFilename());
        foodRepository.save(food);
        return "redirect:/admin/food";
    }
    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
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
