package com.fpt.edu.controllers;

import com.fpt.edu.models.Ban;
import com.fpt.edu.repository.BanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "admin/ban")
public class BanController {
    @Autowired
    BanRepository banRepository;

    @ModelAttribute("danhSachBan")
    public List<Ban> populateTypes() {
        return banRepository.findAll();
    }

    @RequestMapping(value = "")
    public String quanlyban(Model model) {
        return "admin_templates/table_index";
    }

    @RequestMapping(value = "/new")
    public String addForm(Model model) {
        Ban ban = new Ban();
        model.addAttribute("ban", ban);
        return ("admin_templates/table_add_form");
    }

    @PostMapping("/add")
    public String add(@Valid Ban ban,
                      BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin_templates/table_add_form";
        }
        banRepository.save(ban);
        return "redirect:/admin/ban";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Ban ban = banRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Table Id:" + id));

        model.addAttribute("ban", ban);
        model.addAttribute("allTables", banRepository.findAll());
        return "admin_templates/table_edit_form";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") long id, @Valid Ban ban,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            ban.setId(id);
            return "admin_templates/table_edit_form";
        }

        banRepository.save(ban);
        return "redirect:/admin/ban";
    }

    @GetMapping("/delete/{id}")
    public String deleteTable(@PathVariable("id") long id, Model model) {
        Ban ban = banRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Table Id:" + id));
        banRepository.delete(ban);
        return "redirect:/admin/ban";
    }
}
