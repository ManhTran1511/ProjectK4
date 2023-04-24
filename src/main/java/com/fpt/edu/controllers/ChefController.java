package com.fpt.edu.controllers;

import com.fpt.edu.models.Chef;
import com.fpt.edu.repository.ChefRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "admin/chef")
public class ChefController {
    @Autowired
    ChefRepository chefRepository;

    @ModelAttribute("allChefs")
    public List<Chef> chefList(){
        return chefRepository.findAll();
    }

    @RequestMapping(value = "")
    public String chefManager(Model model) {
        return "admin_templates/chef_index";
    }

    @RequestMapping(value = "/new")
    public String addForm(Model model) {
        Chef chef = new Chef();
        model.addAttribute("chef", chef);
        return "admin_templates/chef_add_form";
    }

    @RequestMapping(value = "/add")
    public String add(@Valid Chef chef,
                      BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin_templates/chef_add_form";
        }
        chefRepository.save(chef);
        return "redirect:/admin/chef";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Chef chef = chefRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Chef Id:" + id));

        model.addAttribute("chef", chef);
        model.addAttribute("allChefs", chefRepository.findAll());
        return "admin_templates/chef_edit_form";
    }

    @PostMapping("/update/{id}")
    public String updateChef(@PathVariable("id") long id, @Valid Chef chef,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            chef.setId(id);
            return "admin_templates/chef_edit_form";
        }

        chefRepository.save(chef);
        return "redirect:/admin/chef";
    }

    @GetMapping("/delete/{id}")
    public String deleteChef(@PathVariable("id") long id, Model model) {
        Chef chef = chefRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Chef Id:" + id));
        chefRepository.delete(chef);
        return "redirect:/admin/chef";
    }
}
