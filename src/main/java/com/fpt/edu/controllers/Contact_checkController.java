package com.fpt.edu.controllers;

import com.fpt.edu.models.Contact;
import com.fpt.edu.models.Contact_check;
import com.fpt.edu.repository.ContactRepository;
import com.fpt.edu.repository.Contact_checkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;


@Controller
@RequestMapping(value = "admin/contact_check")

public class Contact_checkController {

    @Autowired
    ContactRepository contactRepository;
    @Autowired
    Contact_checkRepository contact_checkRepository;

    @ModelAttribute("allContact_checks")
    public List<Contact_check> contact_checkList() {
        return contact_checkRepository.findAll();
    }


    @RequestMapping(value = "")
    public String contactManager(Model model) {
        return "admin_templates/contact_check_index";
    }

    @RequestMapping(value = "/new")
    public String addForm(Model model) {
        Contact_check contact_check = new Contact_check();
        model.addAttribute("contact_check", contact_check);
        return "user_templates/contact";
    }

    @RequestMapping(value = "/add")
    public String add(@Valid Contact_check contact_check,
                      BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "user_templates/contact";
        }
        contact_checkRepository.save(contact_check);
        return "redirect:/contact";
    }

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
        return "redirect:/admin/contact_check";
    }

    @GetMapping("/delete/{id}")
    public String deleteContact(@PathVariable("id") long id, Model model) {
        Contact_check contact_check = contact_checkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Contact Id:" + id));
        contact_checkRepository.delete(contact_check);
        return "redirect:/admin/contact_check";
    }
}

