package com.fpt.edu.controllers;

import com.fpt.edu.models.Ban;
import com.fpt.edu.models.Contact;
import com.fpt.edu.models.Contact_check;
import com.fpt.edu.repository.ContactRepository;
import com.fpt.edu.repository.Contact_checkRepository;
import com.fpt.edu.security.services.BanService;
import com.fpt.edu.security.services.ContactCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;


@Controller
@RequestMapping(value = "admin/contact_check")

public class Contact_checkController {

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    Contact_checkRepository contact_checkRepository;

    @Autowired
    private ContactCheckService contactCheckService;

    @ModelAttribute("allContact_checks")
    public List<Contact_check> contact_checkList() {
        return contact_checkRepository.findAll();
    }


    @RequestMapping(value = "")
    public String contactCheckManager(Model model, String keyword) {
        return getOnePage(model, 1, keyword);
//        return "admin_templates/contact_check_index";
    }

    @GetMapping("page/{pageNumber}")
    public String getOnePage(Model model, @PathVariable("pageNumber") int currentPage, String keyword) {
        Page<Contact_check> page;
        int totalPages;
        long totalItems;
        List<Contact_check> contactCheckList;

        if (keyword == null || keyword.isEmpty()) {
            page = contactCheckService.findPage(currentPage);
            totalPages = page.getTotalPages();
            totalItems = page.getTotalElements();
            contactCheckList = page.getContent();
        } else {
            page = contactCheckService.findByKeyword(keyword, currentPage);
            totalPages = page.getTotalPages();
            totalItems = page.getTotalElements();
            contactCheckList = page.getContent();
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("contactCheckList", contactCheckList);


        return "admin_templates/contact_check_index";
    }

    @GetMapping("/page/{pageNumber}/{field}")
    public String getPageWithSort(Model model,
                                  @PathVariable("pageNumber") int currentPage,
                                  @PathVariable String field,
                                  @PathParam("sortDir") String sortDir){

        Page<Contact_check> page = contactCheckService.findAllWithSort(field, sortDir, currentPage);
        List<Contact_check> contactCheckList = page.getContent();
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);

        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc")?"desc":"asc");
        model.addAttribute("contactCheckList", contactCheckList);

        return "admin_templates/contact_check_index";
    }

//    @RequestMapping(value = "/new")
//    public String addForm(Model model) {
//        Contact_check contact_check = new Contact_check();
//        model.addAttribute("contact_check", contact_check);
//        return "user_templates/contact";
//    }
//
//    @RequestMapping(value = "/add")
//    public String add(@Valid Contact_check contact_check,
//                      BindingResult result, Model model) {
//        if (result.hasErrors()) {
//            return "user_templates/contact";
//        }
//        contact_checkRepository.save(contact_check);
//        return "redirect:/contact";
//    }

    @GetMapping("/accept/{id}")
    public String accept(@PathVariable("id") long id, Model model) {
        System.out.println("accept id "+ id);
//        if (result.hasErrors()) {
//            return "user_templates/contact";
//        }
        Contact_check contact_check = contact_checkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Contact Id:" + id));

        Contact contact = new Contact();
        contact.setName(contact_check.getName());
        contact.setEmail(contact_check.getEmail());
        contact.setComment(contact_check.getComment());
        contact.setPhone(contact_check.getPhone());
        contactRepository.save(contact);
        contact_checkRepository.delete(contact_check);
        return "redirect:/admin/contact_check?success";
    }

    @GetMapping("/delete/{id}")
    public String deleteContact(@PathVariable("id") long id, Model model) {
        Contact_check contact_check = contact_checkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Contact Id:" + id));
        contact_checkRepository.delete(contact_check);
        return "redirect:/admin/contact_check?delete";
    }
}

