package com.fpt.edu.controllers;

import com.fpt.edu.models.Ban;
import com.fpt.edu.repository.BanRepository;
import com.fpt.edu.security.services.BanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;

@Controller
@RequestMapping(value = "admin/ban")
public class BanController {
    @Autowired
    BanRepository banRepository;

    @Autowired
    private BanService banService;

    @ModelAttribute("danhSachBan")
    public List<Ban> populateTypes() {
        return banRepository.findAll();
    }

    @GetMapping(value = "")
    public String quanlyban(Model model, String keyword) {

        return getOnePage(model, 1, keyword);
//        return "admin_templates/table_index";
    }

    @GetMapping("page/{pageNumber}")
    public String getOnePage(Model model, @PathVariable("pageNumber") int currentPage, String keyword) {
        Page<Ban> page;
        int totalPages;
        long totalItems;
        List<Ban> banList;

        if (keyword == null || keyword.isEmpty()) {
            page = banService.findPage(currentPage);
            totalPages = page.getTotalPages();
            totalItems = page.getTotalElements();
            banList = page.getContent();
        } else {
            page = banService.findByKeyword(keyword, currentPage);
            totalPages = page.getTotalPages();
            totalItems = page.getTotalElements();
            banList = page.getContent();
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("banList", banList);


        return "admin_templates/table_index";
    }

    @GetMapping("/page/{pageNumber}/{field}")
    public String getPageWithSort(Model model,
                                  @PathVariable("pageNumber") int currentPage,
                                  @PathVariable String field,
                                  @PathParam("sortDir") String sortDir){

        Page<Ban> page = banService.findAllWithSort(field, sortDir, currentPage);
        List<Ban> banList = page.getContent();
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);

        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc")?"desc":"asc");
        model.addAttribute("banList", banList);

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
            return "redirect:/admin/ban/new?error";
        }
        banRepository.save(ban);
        return "redirect:/admin/ban?add";
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
            return "redirect:/admin/ban/edit?error";
        }

        banRepository.save(ban);
        return "redirect:/admin/ban?edit";
    }

    @GetMapping("/delete/{id}")
    public String deleteTable(@PathVariable("id") long id, Model model) {
        Ban ban = banRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Table Id:" + id));
        banRepository.delete(ban);
        return "redirect:/admin/ban?delete";
    }
}
