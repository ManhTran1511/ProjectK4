package com.fpt.edu.controllers;

import com.fpt.edu.models.*;
import com.fpt.edu.repository.*;
import com.fpt.edu.security.services.FoodService;
import com.fpt.edu.security.services.InvoiceService;
import com.fpt.edu.security.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "staff/invoice")
public class InvoiceController {
    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    InvoiceDetailRepository invoiceDetailRepository;

    @Autowired
    FoodOrderRepository foodOrderRepository;

    @Autowired
    BanRepository banRepository;

    @Autowired
    FoodRepository foodRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BanDatRepository banDatRepository;

    @Autowired
    private InvoiceService invoiceService;

    @ModelAttribute("listInvoices")
    public List<Invoice> listInvoices() {
        return invoiceRepository.findAll();
    }

    @RequestMapping(value = "/index")
    public String invoices(Model model, String keyword) throws IOException {
        model.addAttribute("listFoods", foodRepository.findAll());
        model.addAttribute("allCates", categoryRepository.findAll());
        return getOnePage(model, 1, keyword);
    }

    @GetMapping("/index/page/{pageNumber}")
    public String getOnePage(Model model, @PathVariable("pageNumber") int currentPage, String keyword) {
        Page<Invoice> page;
        int totalPages;
        long totalItems;
        List<Invoice> invoiceList;

        if (keyword == null || keyword.isEmpty()) {
            page = invoiceService.findPage(currentPage);
            totalPages = page.getTotalPages();
            totalItems = page.getTotalElements();
            invoiceList = page.getContent();
        } else {
            page = invoiceService.findByKeyword(keyword, currentPage);
            totalPages = page.getTotalPages();
            totalItems = page.getTotalElements();
            invoiceList = page.getContent();
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("invoiceList", invoiceList);
        model.addAttribute("allCates", categoryRepository.findAll());

        return "admin_templates/invoices_index_list";
    }

    @GetMapping("index/page/{pageNumber}/{field}")
    public String getPageWithSort(Model model,
                                  @PathVariable("pageNumber") int currentPage,
                                  @PathVariable String field,
                                  @PathParam("sortDir") String sortDir){

        Page<Invoice> page = invoiceService.findAllWithSort(field, sortDir, currentPage);
        List<Invoice> invoiceList = page.getContent();
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);

        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc")?"desc":"asc");
        model.addAttribute("invoiceList", invoiceList);
        model.addAttribute("allCates", categoryRepository.findAll());

        return "admin_templates/invoices_index_list";
    }

    List<BanDat> tablesBookingList;

    @RequestMapping(value = "")
    public String invoiceManager(Model model) {

        // Lay ngay hien tai
        LocalDate localDate = LocalDate.now();

        // Lay lich
        Calendar c = Calendar.getInstance();

        // Lay thoi gian hien tai
//        int gioDat = c.get(Calendar.HOUR_OF_DAY);
        LocalTime current = LocalTime.now();
        int gioDat = current.getHour();

        // Tao danh sach ban dang su dung
        List<Ban> tableUsingList = new ArrayList<>();

        // Lay danh sach ban dang su dung
        for (BanDat bookingTable : banDatRepository.findAll()) {

            // Lay ngay dat, gio dat, gio ra cua booking
            String bookingTableDate = bookingTable.getNgayDat();
//            int bookingTableTimeIn = Integer.parseInt(bookingTable.getGioDat());
            LocalTime gioBanDaDat = LocalTime.parse((bookingTable.getGioDat()));
            int bookingTableTimeIn = gioBanDaDat.getHour();
//            int checkPhutDatCuaBan = bookingTable.getGioDat().getMinutes();

            LocalTime timeOut = LocalTime.parse(bookingTable.getGioRa());
            int bookingTableTimeOut = timeOut.getHour();

            Ban mainTable = banRepository.findById(bookingTable.getBan_id()).get();

            // Lay ra ban dang su dung tai thoi diem hien tai
            if (Objects.equals(bookingTableDate, String.valueOf(localDate))) {
                if (gioDat >= bookingTableTimeIn && gioDat < bookingTableTimeOut && mainTable.getTrangThai() == 2) {

                    // THem vao danh sach ban dang su dung
                    tableUsingList.add(mainTable);
                }
            }
        }

        model.addAttribute("tableUsingList", tableUsingList);
        return "admin_templates/invoice_list_table";
    }

    long table_id;

    boolean paid = true;

    List <OrderDetail> orderFoodListInvoice = new ArrayList<>();

    List<Ban> changeStatusTables = new ArrayList<>();

    BanDat tableBookingRemove = new BanDat();

    // Tao Array ban da dat
    String[] tablesList;

    // Tao list ban da dat
    List<String> tables;

    @GetMapping("/table/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Ban ban = banRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Table Id:" + id));
        table_id = id;


        Invoice invoice = new Invoice();
        InvoiceDetail invoiceDetail = new InvoiceDetail();

        List <OrderDetail> orderFoodList = new ArrayList<>();
        Boolean foodExist = false;

        for (FoodOrder foodOrder : foodOrderRepository.findAll()){
            if(foodOrder.getTable_id() == table_id) {
                for (OrderDetail orderDetail : orderDetailRepository.findAll()) {
                    if (orderDetail.getOrder_id() == foodOrder.getId()) {
                        for(Food food : foodRepository.findAll()) {
                            if (food.getId() == orderDetail.getFood_id()) {
                                for(OrderDetail foodInListOrder : orderFoodList) {
                                    if (orderFoodList.isEmpty() || foodInListOrder.getFood_id() != food.getId()) {
                                        foodExist = false;
                                    } else {
                                        int oldAmount =  foodInListOrder.getAmount();
                                        foodInListOrder.setAmount(oldAmount + orderDetail.getAmount());
                                        foodExist = true;
                                        break;
                                    }
                                }
                                if (foodExist == false) {
                                    orderFoodList.add(orderDetail);
                                }
                            }
                        }
                        foodExist = false;
                    }
                }
            }
        }

        float totalMoney = 0;

        for(OrderDetail foodMoneyTotal : orderFoodList) {
            float unitPrice = 0;
            for(Food foodMoney : foodRepository.findAll()) {
                if(foodMoneyTotal.getFood_id() == foodMoney.getId()) {
                    unitPrice = foodMoney.getPrice();
                }
            }
            totalMoney = totalMoney + (foodMoneyTotal.getAmount() * unitPrice);
        }
        orderFoodListInvoice = orderFoodList;

        // Lay ngay hien tai
        LocalDate localDate = LocalDate.now();

        // Lay lich
        Calendar c = Calendar.getInstance();

        // Lay thoi gian hien tai
//        int gioDat = c.get(Calendar.HOUR_OF_DAY);
        LocalTime current = LocalTime.now();
        int gioDat = current.getHour();
        int phutDat = current.getMinute();

        List<Ban> changeStatusTablesList = new ArrayList<>();

        BanDat bookingDetail = new BanDat();

        // Lay danh sach ban dang su dung
        for (BanDat bt : banDatRepository.findAll()) {

            // Lay ngay dat, gio dat, gio ra cua booking
            String bookingTableDate = bt.getNgayDat();
//            int bookingTableTimeIn = Integer.parseInt(bt.getGioDat());
            LocalTime gioBanDaDat = LocalTime.parse((bt.getGioDat()));
            int bookingTableTimeIn = gioBanDaDat.getHour();

            LocalTime timeOut = LocalTime.parse(bt.getGioRa());
            int bookingTableTimeOut = timeOut.getHour();

            // Lay ra ban dang su dung tai thoi diem hien tai
            if (Objects.equals(bookingTableDate, String.valueOf(localDate))) {
                if (gioDat >= bookingTableTimeIn && gioDat < bookingTableTimeOut
                        && bt.getBan_id() == table_id) {

                    String[] tables = bt.getTablesBooking();

                    // THem vao danh sach ban
                    for (int i = 0; i < tables.length; i++) {
                        for (Ban tableChangeStatus : banRepository.findAll()) {
                            if (Objects.equals(tableChangeStatus.getName(), tables[i])){
                                changeStatusTablesList.add(tableChangeStatus);
                            }
                        }
                    }
                    tableBookingRemove = bt;
                    break;
                }
            }

        }
        changeStatusTables = changeStatusTablesList;

        // Lay ra table detail
        BanDat tableBookingDetail = tableBookingRemove;

        // Lay Array ban da dat
        tablesList = tableBookingDetail.getTablesBooking();

        // Renew list ban da dat
        tables = new ArrayList<>();

        // Them cac ban da dat vao list
        for (int i = 0; i < tablesList.length; i++) {
            tables.add(tablesList[i]);
        }

        model.addAttribute("invoice", invoice);
        model.addAttribute("invoiceDetail", invoiceDetail);
        model.addAttribute("totalMoney", totalMoney);
        model.addAttribute("orderFoodList", orderFoodList);
        model.addAttribute("ban", ban);
        model.addAttribute("table_id", table_id);
        model.addAttribute("tables", tables);
        model.addAttribute("tableBookingDetail", tableBookingDetail);
        model.addAttribute("allTables", banRepository.findAll());
        model.addAttribute("allFoods", foodRepository.findAll());
        model.addAttribute("allFoodOrders", foodOrderRepository.findAll());
        model.addAttribute("allOrdersDetail", orderDetailRepository.findAll());

        if (totalMoney == 0) {
            return "admin_templates/invoice_index_empty";
        } else {
            return "admin_templates/invoice_index";
        }

    }

    @RequestMapping(value = "/pay")
    public String add(@Valid Invoice invoice,
                      BindingResult result, Model model) {

        Ban ban = banRepository.findById(table_id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Table Id:" + table_id));

        if (result.hasErrors()) {
            return "admin_templates/staff";
        }

//        String dateTimeColonPattern = "yyyy-MM-dd HH:mm:ss";
//        DateTimeFormatter dateTimeColonFormatter = DateTimeFormatter.ofPattern(dateTimeColonPattern);

        LocalDateTime localDateTime = LocalDateTime.now();
        invoice.setDate(localDateTime);
        invoice.setBooking_id(tableBookingRemove.getId());


        invoiceRepository.save(invoice);

        List<InvoiceDetail> invoiceDetailList = new ArrayList<>();

        for(OrderDetail orderDetail : orderFoodListInvoice) {
            InvoiceDetail invoiceDetailUnit = new InvoiceDetail();
            invoiceDetailUnit.setFood_id(orderDetail.getFood_id());
            invoiceDetailUnit.setAmount(orderDetail.getAmount());
            invoiceDetailUnit.setInvoice_id(invoice.getId());
            invoiceDetailList.add(invoiceDetailUnit);
        }

        invoiceDetailRepository.saveAll(invoiceDetailList);

        for (OrderDetail orderDetailToDelete : orderDetailRepository.findAll()){
            for (OrderDetail orderDetailOfTable : orderFoodListInvoice) {
                if(Objects.equals(orderDetailToDelete.getId(), orderDetailOfTable.getId())){
                    orderDetailRepository.delete(orderDetailToDelete);
                }
            }
        }

        for (Ban tableBooking : changeStatusTables) {
            tableBooking.setTrangThai(1);
            banRepository.save(tableBooking);
        }

        // Format time
        String timeColonPattern = "HH:mm:ss";
        DateTimeFormatter timeColonFormatter = DateTimeFormatter.ofPattern(timeColonPattern);

        // Lay thoi gian hien tai
        LocalTime current = LocalTime.now();

//        LocalTime localTime = LocalTime.now();

        tableBookingRemove.setGioRa(timeColonFormatter.format(current));

        banDatRepository.save(tableBookingRemove);

        return "redirect:/staff/success";
    }

    @GetMapping("/index/detail/{id}")
    public String detailTable(@PathVariable("id") long id, Model model) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Invoice Id:" + id));

        // Tao list luu details cua invoice
        List<InvoiceDetail> invoiceDetailList = new ArrayList<>();

        // Them vao list detail cua invoice
        for (InvoiceDetail invoiceDetail : invoiceDetailRepository.findAll()) {
            if (invoiceDetail.getInvoice_id() == id) {
                invoiceDetailList.add(invoiceDetail);
            }
        }

        // Tao obj Booking de luu booking cua invoice
        BanDat bookingOfInvoice = new BanDat();

        for (BanDat booking : banDatRepository.findAll()) {
            if (Objects.equals(booking.getId(), invoice.getBooking_id())) {
                bookingOfInvoice = booking;
                break;
            }
        }
        model.addAttribute("bookingOfInvoice", bookingOfInvoice);
        model.addAttribute("tables", tables);
        model.addAttribute("invoice", invoice);
        model.addAttribute("foodList", foodRepository.findAll());
        model.addAttribute("invoiceDetailList", invoiceDetailList);
        return "admin_templates/invoice_detail";
    }
}
