package com.fpt.edu.controllers;

import com.fpt.edu.models.*;
import com.fpt.edu.repository.*;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.swing.*;
import javax.validation.Valid;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping(value = "staff/order")
public class FoodOrderController {
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

    @ModelAttribute("listFoodOrders")
    public List<FoodOrder> listFoodOrders() {
        return foodOrderRepository.findAll();
    }

    @RequestMapping(value = "")
    public String orderManager(Model model) {

        // Lay ngay hien tai
        LocalDate localDate = LocalDate.now();

        // Lay lich
        Calendar c = Calendar.getInstance();

        // Lay thoi gian hien tai
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
//            int checkPhutDatCuaBan = banDaDat.getGioDat().getMinutes();

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
        model.addAttribute("listTable", banRepository.findAll());
        return "admin_templates/order_list_table";
    }

    long table_id;

    // tao list order cua ban
    List<OrderDetail> tableOrderList;

    @GetMapping("/table/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Ban ban = banRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Table Id:" + id));
        table_id = id;

        OrderDetail orderDetail = new OrderDetail();
        Boolean orderExist = false;

        for (FoodOrder foodOrder : foodOrderRepository.findAll()){
            if(foodOrder.getTable_id() == table_id) {
                orderExist = true;
                break;
            }
        }
        if (orderExist == false) {
            FoodOrder foodOrder = new FoodOrder();
            foodOrder.setTable_id(table_id);
            foodOrderRepository.save(foodOrder);
        }

        // renew list order cua ban
        tableOrderList = new ArrayList<>();

        // lay ra list order cua ban
        for (FoodOrder fo : foodOrderRepository.findAll()) {
            if (fo.getTable_id() == table_id) {
                for (OrderDetail od : orderDetailRepository.findAll()) {
                    if (fo.getId() == od.getOrder_id()) {
                        tableOrderList.add(od);
                    }
                }
            }
        }

        model.addAttribute("allOrdersDetail", orderDetailRepository.findAll());
        model.addAttribute("tableOrderList", tableOrderList);
        model.addAttribute("table_id", table_id);
        model.addAttribute("ban", ban);
        model.addAttribute("orderDetailList", orderDetailList);
        model.addAttribute("orderDetail", orderDetail);
        model.addAttribute("allTables", banRepository.findAll());
        model.addAttribute("allCates", categoryRepository.findAll());
        model.addAttribute("allFoodOrder", foodOrderRepository.findAll());
        model.addAttribute("allFoods", foodRepository.findAll());

        return "admin_templates/order_index";
    }

    List<OrderDetail> orderDetailList = new ArrayList<>();

    @RequestMapping(value = "/add")
    public String add(@Valid OrderDetail orderDetail,
                      BindingResult result, Model model) {

        Boolean odExist = false;
        if (result.hasErrors()) {
            return "admin_templates/order_add_food";
        }
        if(orderDetailList.size() != 0) {
            for(OrderDetail orderDetailInList : orderDetailList) {
                if (orderDetailInList.getFood_id() == orderDetail.getFood_id()) {
                    orderDetailInList.setAmount(orderDetailInList.getAmount() + orderDetail.getAmount());
                    odExist = true;
                    break;
                }
            }
            if(odExist == false && orderDetail.getAmount() > 0) {
                OrderDetail od = new OrderDetail();
                od.setFood_id(orderDetail.getFood_id());
                od.setOrder_id(orderDetail.getOrder_id());
                od.setAmount(orderDetail.getAmount());
                orderDetailList.add(od);
            }

        } else if(orderDetail.getAmount() > 0) {
            OrderDetail od = new OrderDetail();
            od.setFood_id(orderDetail.getFood_id());
            od.setOrder_id(orderDetail.getOrder_id());
            od.setAmount(orderDetail.getAmount());
            orderDetailList.add(od);
        }

        model.addAttribute("orderDetail", orderDetail);
        model.addAttribute("allTables", banRepository.findAll());
        model.addAttribute("allCates", categoryRepository.findAll());
        model.addAttribute("allFoodOrder", foodOrderRepository.findAll());
        model.addAttribute("allFoods", foodRepository.findAll());
        model.addAttribute("orderDetailList", orderDetailList);
        return "redirect:/staff/order/table/" + table_id;
    }

    @RequestMapping(value = "/addList")
    public String addList(Model model) {
        Boolean foodOrderExist = false;

        for (OrderDetail orderDetail : orderDetailList) {
            OrderDetail od = new OrderDetail();
            od.setFood_id(orderDetail.getFood_id());
            od.setOrder_id(orderDetail.getOrder_id());
            od.setAmount(orderDetail.getAmount());

            for(OrderDetail odToSetAmount : orderDetailRepository.findAll()) {
                if(odToSetAmount.getOrder_id() == od.getOrder_id() && odToSetAmount.getFood_id() == od.getFood_id()) {
                    int amount = odToSetAmount.getAmount() + od.getAmount();
                    odToSetAmount.setAmount(amount);
                    orderDetailRepository.save(odToSetAmount);
                    foodOrderExist = true;
                    break;
                } else {
                    foodOrderExist = false;
                }
            }
            if (foodOrderExist == false) {
                orderDetailRepository.save(od);
            }
        }
        orderDetailList.clear();
        return "redirect:/staff/order/table/" + table_id;
    }

    @GetMapping("/delete/{id}")
    public String deleteTable(@PathVariable("id") long id, Model model) {
        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Order Detail Id:" + id));
        orderDetailRepository.delete(orderDetail);
        return "redirect:/staff/order/table/" + table_id;
    }

    // Giam so luong bang danh sach do an
    @GetMapping("/minusAmountFood/{id}")
    public String minusAmountFood(@PathVariable("id") long id, Model model) {
        for (OrderDetail odMinus : orderDetailList) {
            if(odMinus.getFood_id() == id) {
                odMinus.setAmount(odMinus.getAmount() - 1);
                if (odMinus.getAmount() == 0) {
                    orderDetailList.remove(odMinus);
                }
                break;
            }
        }
        return "redirect:/staff/order/table/" + table_id;
    }

    // Tang so luong bang danh sach do an
    @GetMapping("/addAmountFood/{id}")
    public String addAmountFood(@PathVariable("id") long id, Model model) {
        for (OrderDetail odAdd : orderDetailList) {
            if(odAdd.getFood_id() == id) {
                odAdd.setAmount(odAdd.getAmount() + 1);
                break;
            }
        }
        return "redirect:/staff/order/table/" + table_id;
    }

    // Giam so luong bang danh sach do an order
    @GetMapping("/minusAmountFoodOrder/{id}")
    public String minusAmountFoodOrder(@PathVariable("id") long id, Model model) {
        for (OrderDetail odfMinus : tableOrderList) {
            if (odfMinus.getFood_id() == id) {
                for (OrderDetail orderDetail : orderDetailRepository.findAll()) {
                    if (orderDetail.getOrder_id() == odfMinus.getOrder_id() && orderDetail.getFood_id() == id) {
                        orderDetail.setAmount(orderDetail.getAmount() - 1);
                        if (orderDetail.getAmount() == 0) {
                            orderDetailRepository.delete(orderDetail);
                        } else {
                            orderDetailRepository.save(orderDetail);
                        }
                        break;
                    }
                }
                break;
            }
        }
        return "redirect:/staff/order/table/" + table_id;
    }

    // Tang so luong bang danh sach do an order
    @GetMapping("/addAmountFoodOrder/{id}")
    public String addAmountFoodOrder(@PathVariable("id") long id, Model model) {
        for (OrderDetail odfAdd : tableOrderList) {
            if (odfAdd.getFood_id() == id) {
                for (OrderDetail orderDetail : orderDetailRepository.findAll()) {
                    if (orderDetail.getOrder_id() == odfAdd.getOrder_id() && orderDetail.getFood_id() == id) {
                        orderDetail.setAmount(orderDetail.getAmount() + 1);
                        orderDetailRepository.save(orderDetail);
                        break;
                    }
                }
                break;
            }
        }
        return "redirect:/staff/order/table/" + table_id;
    }
}
