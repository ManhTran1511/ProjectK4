package com.fpt.edu.controllers;
import com.fpt.edu.models.Contact;
import com.fpt.edu.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "admin/contact")
public class ContactController {

    @Autowired
    ContactRepository contactRepository;

    @ModelAttribute("allContacts")
    public List<Contact> contactList() {
        return contactRepository.findAll();
    }


    @RequestMapping(value = "")
    public String contactManager(Model model) {
        return "admin_templates/contact_index";
    }

    @RequestMapping(value = "/new")
    public String addForm(Model model) {
        Contact contact = new Contact();
        model.addAttribute("contact", contact);
        return "admin_templates/contact_add_form";
    }

    @RequestMapping(value = "/add")
    public String add(@Valid Contact contact,
                      BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin_templates/contact_add_form";
        }
        contactRepository.save(contact);
        return "redirect:/admin/contact";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Contact Id:" + id));

        model.addAttribute("contact", contact);
        model.addAttribute("allContacts", contactRepository.findAll());
        return "admin_templates/contact_edit_form";
    }

    @PostMapping("/update/{id}")
    public String updateContact(@PathVariable("id") long id, @Valid Contact contact,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            contact.setId(id);
            return "admin_templates/contact_edit_form";
        }
        contactRepository.save(contact);
        return "redirect:/admin/contact";
    }

    @GetMapping("/delete/{id}")
    public String deleteContact(@PathVariable("id") long id, Model model) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid contact Id:" + id));
        contactRepository.delete(contact);
        return "redirect:/admin/contact";
    }
}
