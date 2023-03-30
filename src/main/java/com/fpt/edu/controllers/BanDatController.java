package com.fpt.edu.controllers;

import com.fpt.edu.models.BanDat;
import com.fpt.edu.models.Category;
import com.fpt.edu.repository.BanDatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public List<BanDat> populateTypes() {
        return banDatRepository.findAll();
    }

    @RequestMapping(value = "")
    public String bookingManager(Model model) {
        return "admin_templates/booking_add_form";
    }

    @RequestMapping(value = "/new")
    public String addForm(Model model) {
        BanDat banDat = new BanDat();
        Date now = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        model.addAttribute("booking", banDat);
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
        return "redirect:/staff";
    }
}
