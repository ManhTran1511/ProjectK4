package com.fpt.edu.controllers;

import com.fpt.edu.models.Chef;
import com.fpt.edu.repository.ChefRepository;
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

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "admin/chef")
public class ChefController {
    @Autowired
    ChefRepository chefRepository;

    private final StorageService storageService;

    @Autowired
    public ChefController(StorageService storageService) {
        this.storageService = storageService;
    }

    @ModelAttribute("allChefs")
    public List<Chef> chefList(){
        return chefRepository.findAll();
    }

    @RequestMapping(value = "")
    public String chefManager(Model model) throws IOException {
        storageService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(ChefController.class,
                                "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList());
        model.addAttribute("listChefs", chefRepository.findAll());
        return "admin_templates/chef_index";
    }

    @RequestMapping(value = "/new")
    public String addForm(Model model) {
        Chef chef = new Chef();
        model.addAttribute("chef", chef);
        return "admin_templates/chef_add_form";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @RequestMapping(value = "/add")
    public String add(@Valid Chef chef,
                      BindingResult result, Model model, @RequestParam("file") MultipartFile file) {
        if (result.hasErrors()) {
            return "redirect:/admin/chef/new?error";
        }

        storageService.store(file);

        chef.setImage(file.getOriginalFilename());

        chefRepository.save(chef);
        return "redirect:/admin/chef?success";
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
                             BindingResult result, Model model, @RequestParam("file") MultipartFile file) {
        if (result.hasErrors()) {
            chef.setId(id);
            return "redirect:/admin/chef/edit?error";
        }

        storageService.store(file);

        chef.setImage(file.getOriginalFilename());
        chefRepository.save(chef);
        return "redirect:/admin/chef?edit";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteChef(@PathVariable("id") long id, Model model) {
        Chef chef = chefRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Chef Id:" + id));
        chefRepository.delete(chef);
        return "redirect:/admin/chef?delete";
    }
}
