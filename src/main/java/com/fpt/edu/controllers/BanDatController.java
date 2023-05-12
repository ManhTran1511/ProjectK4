package com.fpt.edu.controllers;

import com.fpt.edu.models.Ban;
import com.fpt.edu.models.BanDat;
import com.fpt.edu.repository.BanDatRepository;
import com.fpt.edu.repository.BanRepository;
import com.fpt.edu.security.services.BanDatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping(value = "staff/booking")
public class BanDatController {
    @Autowired
    BanDatRepository banDatRepository;

    @Autowired
    BanRepository banRepository;

    @Autowired
    private BanDatService banDatService;

    @ModelAttribute("allBookings")
    public List<BanDat> bookingList() {
        return banDatRepository.findAll();
    }

    @RequestMapping(value = "")
    public String bookingManager(Model model, String keyword) {
        listBanCheck = new ArrayList<>();
        reLoadBooking = new BanDat();
        checkDropTable = true;
        tableNameList = new ArrayList<>();

        // Lay ra ngay hien tai
        LocalDate localDate = LocalDate.now();
        String today = String.valueOf(localDate);

        model.addAttribute("today", today);
        model.addAttribute("tables", tables);
        model.addAttribute("freeSlot", freeSlot);
        model.addAttribute("allTables", banRepository.findAll());
        return getOnePage(model, 1, keyword);
    }


    @GetMapping("page/{pageNumber}")
    public String getOnePage(Model model, @PathVariable("pageNumber") int currentPage, String keyword) {
        Page<BanDat> page;
        int totalPages;
        long totalItems;
        List<BanDat> banDatList;

        if (keyword == null || keyword.isEmpty()) {
            page = banDatService.findPage(currentPage);
            totalPages = page.getTotalPages();
            totalItems = page.getTotalElements();
            banDatList = page.getContent();
        } else {
            page = banDatService.findByKeyword(keyword, currentPage);
            totalPages = page.getTotalPages();
            totalItems = page.getTotalElements();
            banDatList = page.getContent();
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("banDatList", banDatList);
        model.addAttribute("allTables", banRepository.findAll());

        return "admin_templates/booking_index";
    }

    @GetMapping("/page/{pageNumber}/{field}")
    public String getPageWithSort(Model model,
                                  @PathVariable("pageNumber") int currentPage,
                                  @PathVariable String field,
                                  @PathParam("sortDir") String sortDir){

        Page<BanDat> page = banDatService.findAllWithSort(field, sortDir, currentPage);
        List<BanDat> banDatList = page.getContent();
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);

        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc")?"desc":"asc");
        model.addAttribute("banDatList", banDatList);
        model.addAttribute("allTables", banRepository.findAll());

        return "admin_templates/booking_index";
    }

    List<Ban> listBanCheck = new ArrayList<>();
    BanDat banCheck = new BanDat();

    List<String> tableBookingList = new ArrayList<>();

    String[] tbl;

    Long id_table;

    int freeSlot;

    Boolean checkAmount = false;

    BanDat reLoadBooking = new BanDat();

    @RequestMapping(value = "/new")
    public String addForm(Model model) {
        BanDat banDat = new BanDat();
        Date now = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(now);

        checkAmount = false;

        freeSlot = 0;

        if (listBanCheck.size() == 0) {
            for (Ban allSlot : banRepository.findAll()) {
                freeSlot += allSlot.getLoaiBan();
            }
        } else {
            for (Ban checkSlotBan : listBanCheck) {
                freeSlot += checkSlotBan.getLoaiBan();
            }
        }

        model.addAttribute("reLoadBooking", reLoadBooking);
        model.addAttribute("freeSlot", freeSlot);
        model.addAttribute("listBanCheck", listBanCheck);
        model.addAttribute("tableBookingList", tableBookingList);
        model.addAttribute("banDat", banDat);
        model.addAttribute("date", c);
        model.addAttribute("id_table", id_table);
        return "admin_templates/booking_add_form";
    }

    @RequestMapping(value = "/renew")
    public String addFormRenew(Model model) {
        BanDat banDat = new BanDat();
        Date now = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(now);

        freeSlot = 0;
        checkAmount = false;

        if (listBanCheck.size() == 0) {
            for (Ban allSlot : banRepository.findAll()) {
                freeSlot += allSlot.getLoaiBan();
            }
        } else {
            for (Ban checkSlotBan : listBanCheck) {
                freeSlot += checkSlotBan.getLoaiBan();
            }
        }

        model.addAttribute("reLoadBooking", reLoadBooking);
        model.addAttribute("freeSlot", freeSlot);
        model.addAttribute("listBanCheck", listBanCheck);
        model.addAttribute("tableBookingList", tableBookingList);
        model.addAttribute("banDat", banDat);
        model.addAttribute("date", c);
        model.addAttribute("id_table", id_table);
        return "admin_templates/booking_add_form_false";
    }


    @RequestMapping(value = "/check")
    public String check(@Valid BanDat banDat,
                      BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin_templates/booking_add_form";
        }

        String h = banDat.getGioDat();
        LocalTime bookingTime = LocalTime.parse(h);

        int checkGioDat = bookingTime.getHour();
        int checkPhutDat = bookingTime.getMinute();

//        int checkGioDat = Integer.parseInt(banDat.getGioDat());
        if(checkGioDat >= 10 && checkGioDat < 14) {
            banDat.setGioRa("14:00");
        } else if (checkGioDat >= 18 && checkGioDat < 22) {
            banDat.setGioRa("22:00");
        }

        banCheck = banDat;

        listBanCheck = banRepository.findAll();

        Boolean checkTable = false;

        freeSlot = 0;

        for (BanDat banDaDat : banDatRepository.findAll()){
            if(Objects.equals(banDaDat.getNgayDat(), banDat.getNgayDat()) && !Objects.equals(banDaDat.getStatus(), "Canceled")) {
//                int checkGioDatCuaBan = Integer.parseInt(banDaDat.getGioDat());
                LocalTime gioBanDaDat = LocalTime.parse((banDaDat.getGioDat()));
                int checkGioDatCuaBan = gioBanDaDat.getHour();
//                int checkPhutDatCuaBan = banDaDat.getGioDat().getMinutes();
//                int checkGioRaCuaBan = Integer.parseInt(banDaDat.getGioRa());
                LocalTime timeOut = LocalTime.parse(banDaDat.getGioRa());
                int checkGioRaCuaBan = timeOut.getHour();

                if (checkGioDat < 14 && checkGioDatCuaBan <14 && checkGioDat <= checkGioRaCuaBan) {
                    for (String tableName : banDaDat.getTablesBooking()) {
                        for (Ban ban : listBanCheck) {
                            if(Objects.equals(tableName, ban.getName())) {
                                listBanCheck.remove(ban);
                                break;
                            }
                        }
                    }
                } else if (checkGioDat >= 18 && checkGioDatCuaBan >= 18 && checkGioDat <= checkGioRaCuaBan){
                    for (String tableName : banDaDat.getTablesBooking()) {
                        for (Ban ban : listBanCheck) {
                            if(Objects.equals(tableName, ban.getName())) {
                                listBanCheck.remove(ban);
                                break;
                            }
                        }
                    }
                }
            }
        }

        for (Ban checkSlotBan : listBanCheck) {
            freeSlot += checkSlotBan.getLoaiBan();
        }

        if (banDat.getSoNguoi() <= freeSlot) {
            checkAmount = true;
        } else {
            checkAmount = false;
        }

        reLoadBooking  = banDat;

        model.addAttribute("reLoadBooking", reLoadBooking);
        model.addAttribute("freeSlot", freeSlot);
        model.addAttribute("checkAmount", checkAmount);
        model.addAttribute("id_table", id_table);
        model.addAttribute("banDat", banDat);
        model.addAttribute("banCheck", banCheck);
        model.addAttribute("listBanCheck", listBanCheck);

        if (checkAmount) {
            return "redirect:/staff/booking/new";
        } else {
            return "redirect:/staff/booking/renew";
        }
//        return "redirect:/staff/booking/new";
    }

    @RequestMapping(value = "/checkEdit")
    public String checkEdit(@Valid BanDat banDat,
                        BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin_templates/booking_add_form";
        }

        String h = banDat.getGioDat();
        LocalTime bookingTime = LocalTime.parse(h);

        int checkGioDat = bookingTime.getHour();
        int checkPhutDat = bookingTime.getMinute();

//        int checkGioDat = Integer.parseInt(banDat.getGioDat());
        if(checkGioDat >= 10 && checkGioDat < 14) {
            banDat.setGioRa("14:00");
        } else if (checkGioDat >= 18 && checkGioDat < 22) {
            banDat.setGioRa("22:00");
        }

        banCheck = banDat;

        listBanCheck = banRepository.findAll();

        Boolean checkTable = false;

        freeSlot = 0;

        for (BanDat banDaDat : banDatRepository.findAll()){
            if(Objects.equals(banDaDat.getNgayDat(), banDat.getNgayDat()) && !Objects.equals(banDaDat.getStatus(), "Canceled")) {
//                int checkGioDatCuaBan = Integer.parseInt(banDaDat.getGioDat());
                LocalTime gioBanDaDat = LocalTime.parse((banDaDat.getGioDat()));
                int checkGioDatCuaBan = gioBanDaDat.getHour();

//                int checkPhutDatCuaBan = banDaDat.getGioDat().getMinutes();
//                int checkGioRaCuaBan = Integer.parseInt(banDaDat.getGioRa());

                LocalTime timeOut = LocalTime.parse(banDaDat.getGioRa());
                int checkGioRaCuaBan = timeOut.getHour();

                if (checkGioDat < 14 && checkGioDatCuaBan <14 && checkGioDat <= checkGioRaCuaBan) {
                    for (String tableName : banDaDat.getTablesBooking()) {
                        for (Ban ban : listBanCheck) {
                            if(Objects.equals(tableName, ban.getName())) {
                                listBanCheck.remove(ban);
                                break;
                            }
                        }
                    }
                } else if (checkGioDat >= 18 && checkGioDatCuaBan >= 18 && checkGioDat <= checkGioRaCuaBan){
                    for (String tableName : banDaDat.getTablesBooking()) {
                        for (Ban ban : listBanCheck) {
                            if(Objects.equals(tableName, ban.getName())) {
                                listBanCheck.remove(ban);
                                break;
                            }
                        }
                    }
                }
            }
        }

        for (Ban checkSlotBan : listBanCheck) {
            freeSlot += checkSlotBan.getLoaiBan();
        }

        if (banDat.getSoNguoi() <= freeSlot) {
            checkAmount = true;
        } else {
            checkAmount = false;
        }

        reLoadBooking  = banDat;

        model.addAttribute("reLoadBooking", reLoadBooking);
        model.addAttribute("freeSlot", freeSlot);
        model.addAttribute("checkAmount", checkAmount);
        model.addAttribute("id_table", id_table);
        model.addAttribute("banDat", banDat);
        model.addAttribute("banCheck", banCheck);
        model.addAttribute("listBanCheck", listBanCheck);

        if (checkAmount) {
            return "redirect:/staff/booking/edit/" + idDropTable;
        } else {
            return "redirect:/staff/booking/renew";
        }
//        return "redirect:/staff/booking/new";
    }

    @RequestMapping(value = "/add")
    public String add(@Valid BanDat banDat, Model model) {

        tbl = banDat.getTablesBooking();

        for (String tableName : tbl) {
            for (Ban ban : banRepository.findAll()) {
                if(Objects.equals(ban.getName(), tableName)) {
                    banCheck.setBan_id(ban.getId());
                    break;
                }
            }
            break;
        }
        BanDat testBan = banCheck;

        banCheck.setStatus("Waiting");
        banCheck.setTablesBooking(tbl);
        banDatRepository.save(banCheck);
        reLoadBooking = new BanDat();
        return "redirect:/staff/booking?add";
    }


    Long idDropTable;

    List<String> tableNameList = new ArrayList<>();

    Boolean checkDropTable = true;

    @GetMapping("/edit/{id}")
    public String editBooking(@PathVariable("id") long id, Model model) {
        BanDat banDat = banDatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Booking Id:" + id));

        idDropTable = id;

        String[] tableList = banDat.getTablesBooking();

        if (tableNameList.isEmpty() && checkDropTable) {
            for (int i = 0; i < tableList.length; i++) {
                tableNameList.add(tableList[i]);
            }
        }

        model.addAttribute("tableNameList", tableNameList);
        model.addAttribute("listBanCheck", listBanCheck);
        model.addAttribute("reLoadBooking", reLoadBooking);
        model.addAttribute("freeSlot", freeSlot);
        model.addAttribute("checkAmount", checkAmount);
        model.addAttribute("idDropTable", idDropTable);
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

        BanDat tableChangeStatus = banDatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Booking Id:" + id));

        tableChangeStatus.setStatus(banDat.getStatus());

        // Lay ra ngay vs gio hien tai
        LocalDate localDate = LocalDate.now();
        String today = String.valueOf(localDate);

        LocalTime current = LocalTime.now();
        int gioDat = current.getHour();

        // Lay ra gio dat cua booking
        LocalTime gioBanDaDat = LocalTime.parse((tableChangeStatus.getGioDat()));
        int checkGioDatCuaBan = gioBanDaDat.getHour();

        // Kiem tra xem booking co phai ngay hom nay khong
        if (Objects.equals(today, tableChangeStatus.getNgayDat())) {

            // Kiem tra xem booking va thoi gian hien tai co cung 1 ca lam viec khong
            if(gioDat >= 10 && gioDat < 14) {
                if (checkGioDatCuaBan >= 10 && checkGioDatCuaBan < 14){

                    // Kiem tra xem status co phai Waiting khong
                    if (Objects.equals(tableChangeStatus.getStatus(), "Waiting")) {

                        // Thay doi trang thai cua ban khi thay doi status cua booking
                        for (String table : tableChangeStatus.getTablesBooking()) {
                            for (Ban ban : banRepository.findAll()) {
                                if (Objects.equals(table, ban.getName())) {

                                    // Ban trong khi status = waiting
                                    ban.setTrangThai(1);
                                    banRepository.save(ban);
                                }
                            }
                        }
                    }

                    // Kiem tra xem status co phai Checked khong
                    if (Objects.equals(tableChangeStatus.getStatus(), "Checked")) {

                        // Thay doi trang thai cua ban khi thay doi status cua booking
                        for (String table : tableChangeStatus.getTablesBooking()) {
                            for (Ban ban : banRepository.findAll()) {
                                if (Objects.equals(table, ban.getName())) {

                                    // Ban dang su dung khi status = Checked
                                    ban.setTrangThai(2);
                                    banRepository.save(ban);
                                }
                            }
                        }
                    }

                    // Kiem tra xem status co phai Canceled khong
                    if (Objects.equals(tableChangeStatus.getStatus(), "Canceled")) {

                        // Thay doi trang thai cua ban khi thay doi status cua booking
                        for (String table : tableChangeStatus.getTablesBooking()) {
                            for (Ban ban : banRepository.findAll()) {
                                if (Objects.equals(table, ban.getName())) {

                                    // Ban trong khi status = Canceled
                                    ban.setTrangThai(1);
                                    banRepository.save(ban);
                                }
                            }
                        }
                    }
                }
            } else if (gioDat >= 18 && gioDat < 22) {
                if (checkGioDatCuaBan >= 18 && checkGioDatCuaBan < 22){
                    // Kiem tra xem status co phai Waiting khong
                    if (Objects.equals(tableChangeStatus.getStatus(), "Waiting")) {

                        // Thay doi trang thai cua ban khi thay doi status cua booking
                        for (String table : tableChangeStatus.getTablesBooking()) {
                            for (Ban ban : banRepository.findAll()) {
                                if (Objects.equals(table, ban.getName())) {

                                    // Ban trong khi status = waiting
                                    ban.setTrangThai(1);
                                    banRepository.save(ban);
                                }
                            }
                        }
                    }

                    // Kiem tra xem status co phai Checked khong
                    if (Objects.equals(tableChangeStatus.getStatus(), "Checked")) {

                        // Thay doi trang thai cua ban khi thay doi status cua booking
                        for (String table : tableChangeStatus.getTablesBooking()) {
                            for (Ban ban : banRepository.findAll()) {
                                if (Objects.equals(table, ban.getName())) {

                                    // Ban dang su dung khi status = Checked
                                    ban.setTrangThai(2);
                                    banRepository.save(ban);
                                }
                            }
                        }
                    }

                    // Kiem tra xem status co phai Canceled khong
                    if (Objects.equals(tableChangeStatus.getStatus(), "Canceled")) {

                        // Thay doi trang thai cua ban khi thay doi status cua booking
                        for (String table : tableChangeStatus.getTablesBooking()) {
                            for (Ban ban : banRepository.findAll()) {
                                if (Objects.equals(table, ban.getName())) {

                                    // Ban trong khi status = Canceled
                                    ban.setTrangThai(1);
                                    banRepository.save(ban);
                                }
                            }
                        }
                    }
                }
            }
        }
        banDatRepository.save(tableChangeStatus);
//        return "redirect:/staff/booking";
        return "redirect:/staff/booking/detail/" + id;
    }

    @GetMapping("/drop/{name}")
    public String dropTable(@PathVariable("name") String name, Model model) {
        BanDat banDat = banDatRepository.findById(idDropTable)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Booking Id:" + idDropTable));
        String[] tableBookingList = banDat.getTablesBooking();
        for (int i = 0; i < tableBookingList.length; i++) {
            if(Objects.equals(tableBookingList[i], name)) {
                tableBookingList[i] = "";
                break;
            }
        }

        for (String tbnl : tableNameList) {
            if (Objects.equals(tbnl, name)) {
                tableNameList.remove(tbnl);
                for (Ban tableReturn : banRepository.findAll()) {
                    if (Objects.equals(tableReturn.getName(), tbnl)) {
                        listBanCheck.add(tableReturn);
                        break;
                    }
                }
                break;
            }
        }

        if (tableNameList.isEmpty()) {
            checkDropTable = false;
        }

        model.addAttribute("tableNameList", tableNameList);
        banDat.setTablesBooking(tableBookingList);
        return "redirect:/staff/booking/edit/" + idDropTable;
    }



    @GetMapping("/delete/{id}")
    public String deleteTable(@PathVariable("id") long id, Model model) {
        BanDat banDat = banDatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Booking Id:" + id));
        banDatRepository.delete(banDat);
        return "redirect:/staff/booking";
    }

    @GetMapping("/checkIn/{id}")
    public String checkInBooking(@PathVariable("id") long id, Model model) {
        BanDat banDat = banDatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Booking Id:" + id));

        if (Objects.equals(banDat.getStatus(), "Waiting")) {
            banDat.setStatus("Checked");
        }

        // Thay doi trang thai cua ban khi quick booking
        for (String table : banDat.getTablesBooking()) {
            for (Ban ban : banRepository.findAll()) {
                if (Objects.equals(table, ban.getName())) {
                    ban.setTrangThai(2);
                }
            }
        }

        banDatRepository.save(banDat);

        return "redirect:/staff/booking";
    }

    @GetMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable("id") long id, Model model) {
        BanDat banDat = banDatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Booking Id:" + id));

        if (Objects.equals(banDat.getStatus(), "Waiting")) {
            banDat.setStatus("Canceled");
        }
        banDatRepository.save(banDat);

        return "redirect:/staff/booking";
    }


    // Tao Array ban da dat
    String[] tablesList;

    // Tao list ban da dat
    List<String> tables;
    @GetMapping("/detail/{id}")
    public String detailTable(@PathVariable("id") long id, Model model) {
        // Lay ra booking theo id
        BanDat bookingDetail = banDatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Booking Id:" + id));

        // Tao obj booking de thay doi status
        BanDat banDat = new BanDat();

        // Lay Array ban da dat
        tablesList = bookingDetail.getTablesBooking();

        // Renew list ban da dat
        tables = new ArrayList<>();

        // Them cac ban da dat vao list
        for (int i = 0; i < tablesList.length; i++) {
            tables.add(tablesList[i]);
        }

        model.addAttribute("banDat", banDat);
        model.addAttribute("tables", tables);
        model.addAttribute("bookingDetail", bookingDetail);
        return "admin_templates/booking_details";
    }

    @RequestMapping(value = "/menu")
    @GetMapping("/menu/{id}")
    public String bookingMenu(@PathVariable("id") long id, Model model) {
        BanDat banDat = banDatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Booking Id:" + id));

        model.addAttribute("banDat", banDat);
        model.addAttribute("allBookings", banDatRepository.findAll());
        return "admin_templates/booking_index";
    }

    // Tao danh sach main table khi quick booking
    List<Ban> tableOrderNow = new ArrayList<>();
    @RequestMapping(value = "quickBooking")
    public String quickBooking(@Valid BanDat banDat, Model model) {

        LocalDate localDate = LocalDate.now();

        Calendar c = Calendar.getInstance();

        // Format time
        String timeColonPattern = "HH:mm:ss";
        DateTimeFormatter timeColonFormatter = DateTimeFormatter.ofPattern(timeColonPattern);

//        int gioDat = c.get(Calendar.HOUR_OF_DAY);
//        int phutDat = c.get(Calendar.MINUTE);
//
        LocalTime current = LocalTime.now();
        int gioDat = current.getHour();
        int phutDat = current.getMinute();

        if(gioDat >= 10 && gioDat < 14) {
            banDat.setGioRa("14:00");
        } else if (gioDat >= 18 && gioDat < 22) {
            banDat.setGioRa("22:00");
        }

        banDat.setGioDat(timeColonFormatter.format(current));
        banDat.setNgayDat(String.valueOf(localDate));

        // Set id ban cho booking va them vao danh sach order sau khi quick booking
        for (String table : banDat.getTablesBooking()) {
            for (Ban ban : banRepository.findAll()) {
                if (Objects.equals(table, ban.getName())) {
                    banDat.setBan_id(ban.getId());
                    tableOrderNow.add(ban);
                    break;
                }
            }
            break;
        }

        // Thay doi trang thai cua ban khi quick booking
        for (String table : banDat.getTablesBooking()) {
            for (Ban ban : banRepository.findAll()) {
                if (Objects.equals(table, ban.getName())) {
                    ban.setTrangThai(2);
                }
            }
        }
        banDat.setSdt("0");
        banDat.setStatus("Checked");

        banDatRepository.save(banDat);

        return "redirect:/staff/booking";
    }

}
