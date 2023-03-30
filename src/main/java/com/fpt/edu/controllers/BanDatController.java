package com.fpt.edu.controllers;

import com.fpt.edu.models.BanDat;
import com.fpt.edu.models.Category;
import com.fpt.edu.repository.BanDatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.TimeZone;

@Controller
@RequestMapping(value = "staff/booking")
public class BanDatController {
    @Autowired
    BanDatRepository banDatRepository;
    @ModelAttribute("allBookings")
    public List<BanDat> bookingList() {
        return banDatRepository.findAll();
    }

    @RequestMapping(value = "")
    public String bookingManager(Model model) {
        return "admin_templates/booking_index";
    }

    @RequestMapping(value = "/new")
    public String addForm(Model model) {
        BanDat banDat = new BanDat();
        Date now = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        model.addAttribute("banDat", banDat);
        model.addAttribute("date", c);
        return "admin_templates/booking_add_form";
    }

    @RequestMapping(value = "/add")
    public String add(@Valid BanDat banDat,
                      BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin_templates/booking_add_form";
        }
        banDatRepository.save(banDat);
        return "redirect:/staff/booking";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        BanDat banDat = banDatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Booking Id:" + id));

        model.addAttribute("banDat", banDat);
        model.addAttribute("allBookings", banDatRepository.findAll());
        return "admin_templates/booking_edit_form";
    }

    @PostMapping("/update/{id}")
    public String updateBooking(@PathVariable("id") long id, @Valid BanDat banDat,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            banDat.setId(id);
            return "admin_templates/booking_edit_form";
        }

        banDatRepository.save(banDat);
        return "redirect:/staff/booking";
    }

    @GetMapping("/delete/{id}")
    public String deleteTable(@PathVariable("id") long id, Model model) {
        BanDat banDat = banDatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Booking Id:" + id));
        banDatRepository.delete(banDat);
        return "redirect:/staff/booking";
    }
}
