package com.fpt.edu.controllers;
import com.fpt.edu.models.Ban;
import com.fpt.edu.models.Contact;
import com.fpt.edu.repository.ContactRepository;
import com.fpt.edu.security.services.BanService;
import com.fpt.edu.security.services.ContactService;
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
@RequestMapping(value = "admin/contact")
public class ContactController {

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    private ContactService contactService;

    @ModelAttribute("allContacts")
    public List<Contact> contactList() {
        return contactRepository.findAll();
    }


    @RequestMapping(value = "")
    public String contactManager(Model model, String keyword) {
        return getOnePage(model, 1, keyword);
//        return "admin_templates/contact_index";
    }

    @GetMapping("page/{pageNumber}")
    public String getOnePage(Model model, @PathVariable("pageNumber") int currentPage, String keyword) {
        Page<Contact> page;
        int totalPages;
        long totalItems;
        List<Contact> contactList;

        if (keyword == null || keyword.isEmpty()) {
            page = contactService.findPage(currentPage);
            totalPages = page.getTotalPages();
            totalItems = page.getTotalElements();
            contactList = page.getContent();
        } else {
            page = contactService.findByKeyword(keyword, currentPage);
            totalPages = page.getTotalPages();
            totalItems = page.getTotalElements();
            contactList = page.getContent();
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("contactList", contactList);


        return "admin_templates/contact_index";
    }

    @GetMapping("/page/{pageNumber}/{field}")
    public String getPageWithSort(Model model,
                                  @PathVariable("pageNumber") int currentPage,
                                  @PathVariable String field,
                                  @PathParam("sortDir") String sortDir){

        Page<Contact> page = contactService.findAllWithSort(field, sortDir, currentPage);
        List<Contact> contactList = page.getContent();
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);

        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc")?"desc":"asc");
        model.addAttribute("contactList", contactList);

        return "admin_templates/contact_index";
    }

    @GetMapping("/delete/{id}")
    public String deleteContact(@PathVariable("id") long id, Model model) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid contact Id:" + id));
        contactRepository.delete(contact);
        return "redirect:/admin/contact?delete";
    }
}
