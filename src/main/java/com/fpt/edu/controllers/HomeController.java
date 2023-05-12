package com.fpt.edu.controllers;

import com.fpt.edu.models.*;
import com.fpt.edu.repository.*;
import com.fpt.edu.security.storage.StorageFileNotFoundException;
import com.fpt.edu.security.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import org.springframework.core.io.Resource;
import javax.validation.Valid;
import javax.validation.Valid;
import java.io.IOException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "")
public class HomeController {

    @Autowired
    FoodRepository foodRepository;

    @Autowired
    GalleryRepository galleryRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ChefRepository chefRepository;

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    Contact_checkRepository contact_checkRepository;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    BanRepository banRepository;

    @Autowired
    BanDatRepository banDatRepository;

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    private final StorageService storageService;

    @Autowired
    public HomeController(StorageService storageService) {
        this.storageService = storageService;
    }

    // USER ROUTES
    @GetMapping("")
    public String index(Model model) throws IOException {
        List<Blog> list3Blogs = new ArrayList<>();
        int count = 0;

        for(Blog blog : blogRepository.findAll()){
            if (count < 3){
                list3Blogs.add(blog);
                count = count + 1;
            } else {
                break;
            }
        }

        List<Gallery> list3Galleries = new ArrayList<>();
        int countGallery = 0;

        for(Gallery gallery : galleryRepository.findAll()){
            if (countGallery < 3){
                list3Galleries.add(gallery);
                countGallery = countGallery + 1;
            } else {
                break;
            }
        }

        storageService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(HomeController.class,
                                "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList());

        model.addAttribute("list3Blogs", list3Blogs);
        model.addAttribute("listContact", contactRepository.findAll());
        model.addAttribute("list3Galleries",list3Galleries );

        return "index";
    }

    @GetMapping("home")
    public String home(Model model) throws IOException {
        List<Blog> list3Blogs = new ArrayList<>();
        int count = 0;

        for(Blog blog : blogRepository.findAll()){
            if (count < 3){
                list3Blogs.add(blog);
                count = count + 1;
            } else {
                break;
            }
        }

        List<Gallery> list3Galleries = new ArrayList<>();
        int countGallery = 0;

        for(Gallery gallery : galleryRepository.findAll()){
            if (countGallery < 3){
                list3Galleries.add(gallery);
                countGallery = countGallery + 1;
            } else {
                break;
            }
        }

        storageService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(HomeController.class,
                                "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList());

        BanDat banDat = new BanDat();
        Date now = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(now);

        int time = c.get(Calendar.HOUR_OF_DAY);

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

        model.addAttribute("list3Blogs", list3Blogs);
        model.addAttribute("listContact", contactRepository.findAll());
        model.addAttribute("list3Galleries",list3Galleries );


        model.addAttribute("time", time);
        model.addAttribute("banDat", banDat);
        return "index";
    }

    List<Ban> listBanCheck = new ArrayList<>();
//    BanDat banCheck = new BanDat();

    List<String> tableBookingList = new ArrayList<>();

    String[] tbl;

    Long id_table;

    int freeSlot;

    Boolean checkAmount = false;

    BanDat reLoadBooking = new BanDat();

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @GetMapping("reservation")
    public String reservation(Model model) {

        BanDat banDat = new BanDat();
        Date now = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(now);

        int time = c.get(Calendar.HOUR_OF_DAY);

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

        model.addAttribute("time", time);
        model.addAttribute("banDat", banDat);
        return "user_templates/reservation";
    }

    @RequestMapping(value = "/reservation/add")
    public String add(@Valid BanDat banDat) {

        String h = banDat.getGioDat();
        LocalTime bookingTime = LocalTime.parse(h);

        int checkGioDat = bookingTime.getHour();
        int checkPhutDat = bookingTime.getMinute();

        if(checkGioDat >= 10 && checkGioDat < 14) {
            banDat.setGioRa("14:00");
        } else if (checkGioDat >= 18 && checkGioDat < 22) {
            banDat.setGioRa("22:00");
        }

//        banCheck = banDat;

        listBanCheck = banRepository.findAll();

        Boolean checkTable = false;

        freeSlot = 0;

        for (BanDat banDaDat : banDatRepository.findAll()){
            if(Objects.equals(banDaDat.getNgayDat(), banDat.getNgayDat())) {

                LocalTime gioBanDaDat = LocalTime.parse((banDaDat.getGioDat()));
                int checkGioDatCuaBan = gioBanDaDat.getHour();

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
                } else if (checkGioDat >= 17 && checkGioDatCuaBan >= 17 && checkGioDat <= checkGioRaCuaBan){
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

        reLoadBooking = banDat;

        // Lay so luong nguoi dat ban
        int amountPeople = banDat.getSoNguoi();

        // Tao list ban dat
        List<Ban> tablesAddList = new ArrayList<>();

        // Tao list de lay loai ban 4
        List<Ban> tables2 = new ArrayList<>();

        // Tao list de lay loai ban 2
        List<Ban> tables4 = new ArrayList<>();

        // Phan chia loai ban 2 va 4 vao trong list
        for (Ban tableType : listBanCheck) {
            if (tableType.getLoaiBan() == 2) {
                tables2.add(tableType);
            } else {
                tables4.add(tableType);
            }
        }

        // Them ban khi dat
        if (checkAmount) {
            while (amountPeople > 0) {

                // Kiem tra so nguoi > 2 va so luong ban 4 trong > 0
                if (amountPeople > 2 && tables4.size() > 0) {
                    for (Ban tb4 : tables4) {

                        // Them ban 4 vao danh sach ban dat
                        tablesAddList.add(tb4);

                        // Giam so luong nguoi dat sau khi them ban 4
                        amountPeople = amountPeople - 4;

                        // Bo ban 4 vua dat ra khoi danh sach ban 4 trong
                        tables4.remove(tb4);
                        break;
                    }
                }

                // Kiem tra 0 < so nguoi < 3 va so luong ban 4 trong = 0
                else if (amountPeople > 2) {
                    for (Ban tb2 : tables2) {

                        // Them ban 2 vao danh sach ban dat
                        tablesAddList.add(tb2);

                        // Giam so luong nguoi dat sau khi them ban 2
                        amountPeople = amountPeople - 2;

                        // Bo ban 2 vua dat ra khoi danh sach ban 2 trong
                        tables2.remove(tb2);
                        break;
                    }
                }


                // Kiem tra 0 < so nguoi < 3 va so luong ban 2 trong > 0
                else if (tables2.size() > 0) {
                    for (Ban tb2 : tables2) {

                        // Them ban 2 vao danh sach ban dat
                        tablesAddList.add(tb2);

                        // Giam so luong nguoi dat sau khi them ban 2
                        amountPeople = amountPeople - 2;

                        // Bo ban 2 vua dat ra khoi danh sach ban 2 trong
                        tables2.remove(tb2);
                        break;
                    }
                }

                // Kiem tra 0 < so nguoi < 3 va so luong ban 2 trong > 0
                else if (tables4.size() > 0) {
                    for (Ban tb4 : tables4) {

                        // Them ban 2 vao danh sach ban dat
                        tablesAddList.add(tb4);

                        // Giam so luong nguoi dat sau khi them ban 2
                        amountPeople = amountPeople - 4;
                        break;
                    }
                }
            }

            // Lay ra list ban dat
            List<Ban> listAdd = tablesAddList;

            // Lay so luong ban dat
            int amountTables = tablesAddList.size();

            // Khoi tao mang voi length = so luong ban
            String[] tablesName = new String[amountTables];

            // Dat gia tri ban_id cho booking
            for (Ban tableName : tablesAddList) {
                banDat.setBan_id(tableName.getId());
                break;
            }

            // Them vao mang cac ban da dat
            for (int i = 0; i < tablesName.length; i++) {
                for (Ban table : listAdd) {
                    tablesName[i] = table.getName();
                    listAdd.remove(table);
                    break;
                }
            }

//            BanDat testBan = banCheck;

//          Format Date

            String datePattern = "yyyy-MM-dd";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(datePattern);
//
            // Format date cho ngay dat cua booking
            LocalDate dateBooking = LocalDate.parse(banDat.getNgayDat());
            banDat.setNgayDat(dateFormatter.format(dateBooking));

            // Set mang ten ban dat va status cho booking
            banDat.setTablesBooking(tablesName);
            banDat.setStatus("Waiting");

            // Luu booking
            banDatRepository.save(banDat);
            reLoadBooking = new BanDat();

            return "redirect:/home";
        } else {
            return "redirect:/reservation";
        }
    }

    @GetMapping("menu")
    public String menu(Model model) {
        storageService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(HomeController.class,
                                "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList());
        model.addAttribute("listFood", foodRepository.findAll());
        model.addAttribute("listCate", categoryRepository.findAll());
        return "user_templates/menu";
    }

    @GetMapping("gallery")
    public String gallery(Model model) {
        storageService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(HomeController.class,
                                "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList());
        model.addAttribute("listGalleries", galleryRepository.findAll());
        return "user_templates/gallery";
    }

    @GetMapping("about")
    public String about(Model model) {
        List<Chef> list3Chefs = new ArrayList<>();
        int count = 0;

        for(Chef chef : chefRepository.findAll()){
            if (count < 3){
                list3Chefs.add(chef);
                count = count + 1;
            } else {
                break;
            }
        }
        model.addAttribute("list3Chefs", list3Chefs);
        model.addAttribute("listChef", chefRepository.findAll());
        return "user_templates/about";
    }

    @GetMapping("blog")
    public String blog(Model model) {
        storageService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(HomeController.class,
                                "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList());
        model.addAttribute("listBlog", blogRepository.findAll());
        return "user_templates/blog";
    }

    @GetMapping("blog_detail")
    public String blog_detail(Model model) {
        return "user_templates/blog_detail";
    }

    @GetMapping("contact")
    public String contact(Model model) {
        Contact_check contact_check = new Contact_check();
        model.addAttribute("contact_check", contact_check);
        model.addAttribute("listContact",contactRepository.findAll());
        return "user_templates/contact";
    }

    @RequestMapping(value = "/contact/add")
    public String add(@Valid Contact_check contact_check,
                      BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "redirect:/contact?error";
        }
        contact_checkRepository.save(contact_check);
        return "redirect:/contact?success";
    }


    // ADMIN ROUTES
    @GetMapping("admin")
    public String admin(Model model) {

        // Lay ra nam hien tai
        Calendar c = Calendar.getInstance();
        int yearNow = c.get(Calendar.YEAR);

        // Lay ra ngay hien tai
        LocalDate localDate = LocalDate.now();
        String today = String.valueOf(localDate);

        // Tao ra obj today profit
        float todayProfit = 0;

        // Lay ra profit hom nay
        for (Invoice invoice : invoiceRepository.findAll()) {
            LocalDate invoiceProfit = LocalDate.from((invoice.getDate()));
            String invoiceProfitString = String.valueOf(invoiceProfit);
            if (Objects.equals(invoiceProfitString, today)) {
                todayProfit = todayProfit + invoice.getTotal();
            }
        }

        // Lay ra nam truoc
        int lastYear = yearNow - 1;

        // Lay ra profit tu hoa don
        float profitYearNow = 0;
        float profitLastYear = 0;

        // Lay ra so luong invoice
        int salesCountYearNow = 0;
        int salesCountLastYear = 0;

        // Profit nam hien tai
        for (Invoice invoice : invoiceRepository.findAll()) {
            if (invoice.getDate().getYear() == yearNow) {
                profitYearNow = profitYearNow + invoice.getTotal();
                salesCountYearNow++;
            }
        }

        // Profit nam truoc
        for (Invoice invoice : invoiceRepository.findAll()) {
            if (invoice.getDate().getYear() == (yearNow -1)) {
                profitLastYear = profitLastYear + invoice.getTotal();
                salesCountLastYear++;
            }
        }

        // Lay ra so luong booking
        int bookingsCount = 0;
        for (BanDat banDat : banDatRepository.findAll()) {
            bookingsCount++;
        }

        // Lay ra so luong feedback
        int feedBacksCount = 0;
        for (Contact contact : contactRepository.findAll()) {
            feedBacksCount++;
        }

        // Lay ra profit 12 thang cua nam hien tai
        int profitM1 = 0;
        int profitM2 = 0;
        int profitM3= 0;
        int profitM4 = 0;
        int profitM5 = 0;
        int profitM6 = 0;
        int profitM7 = 0;
        int profitM8 = 0;
        int profitM9 = 0;
        int profitM10 = 0;
        int profitM11 = 0;
        int profitM12 = 0;

        // Tao obj thang
        int month = 0;

        for (Invoice invoice : invoiceRepository.findAll()) {
            month = invoice.getDate().getMonthValue();
            if (invoice.getDate().getYear() == yearNow) {
                switch (month) {
                    case 1:
                        profitM1+=invoice.getTotal();
                        break;
                    case 2:
                        profitM2+=invoice.getTotal();
                        break;
                    case 3:
                        profitM3+=invoice.getTotal();
                        break;
                    case 4:
                        profitM4+=invoice.getTotal();
                        break;
                    case 5:
                        profitM5+=invoice.getTotal();
                        break;
                    case 6:
                        profitM6+=invoice.getTotal();
                        break;
                    case 7:
                        profitM7+=invoice.getTotal();
                        break;
                    case 8:
                        profitM8+=invoice.getTotal();
                        break;
                    case 9:
                        profitM9+=invoice.getTotal();
                        break;
                    case 10:
                        profitM10+=invoice.getTotal();
                        break;
                    case 11:
                        profitM11+=invoice.getTotal();
                        break;
                    case 12:
                        profitM12+=invoice.getTotal();
                        break;
                    default:
                }
            }
        }

        // Lay sau so thap phan 1 chu so
        DecimalFormat df = new DecimalFormat("#.#");

        float growthRate = 0;

        // Tinh ti le profit giua 2 nam
        if (profitYearNow == 0) {
            growthRate = 0;
        }
        else if (profitYearNow % profitLastYear == 0) {
            growthRate = profitYearNow / profitLastYear * 100;
        } else {
            growthRate = Float.parseFloat(df.format(profitYearNow / profitLastYear * 100));
        }

        model.addAttribute("todayProfit", todayProfit);
        model.addAttribute("growthRate", growthRate);
        model.addAttribute("yearNow", yearNow);
        model.addAttribute("lastYear", lastYear);
        model.addAttribute("profitM1", profitM1);
        model.addAttribute("profitM2", profitM2);
        model.addAttribute("profitM3", profitM3);
        model.addAttribute("profitM4", profitM4);
        model.addAttribute("profitM5", profitM5);
        model.addAttribute("profitM6", profitM6);
        model.addAttribute("profitM7", profitM7);
        model.addAttribute("profitM8", profitM8);
        model.addAttribute("profitM9", profitM9);
        model.addAttribute("profitM10", profitM10);
        model.addAttribute("profitM11", profitM11);
        model.addAttribute("profitM12", profitM12);
        model.addAttribute("cateList", categoryRepository.findAll());
        model.addAttribute("profitYearNow", profitYearNow);
        model.addAttribute("profitLastYear", profitLastYear);
        model.addAttribute("bookingsCount", bookingsCount);
        model.addAttribute("salesCountLastYear", salesCountLastYear);
        model.addAttribute("salesCountYearNow", salesCountYearNow);
        model.addAttribute("feedBacksCount", feedBacksCount);
        return "admin_templates/index";
    }

    @GetMapping("ban")
    public String ban(Model model) {
        return "admin_templates/table_index";
    }

    @GetMapping("cate")
    public String cate(Model model) {
        return "admin_templates/cate_index";
    }

    @GetMapping("food")
    public String food(Model model) {
        return "admin_templates/food_index";
    }
    @GetMapping("table_add_form")
    public String table_add_form(Model model) {
        return "admin_templates/table_add_form";
    }

    @GetMapping("staff")
    public String staff(Model model) {
        BanDat banDat = new BanDat();

        // Tao list ban trong o hien tai
        List<Ban> tablesEmptyList = new ArrayList<>();

        // Tao bien luu tru cho trong trong nha hang hien tai tinh theo ban
        int freeSlot = 0;

        // Lay ra ngay vs gio hien tai
        LocalDate localDate = LocalDate.now();
        String today = String.valueOf(localDate);

        LocalTime current = LocalTime.now();
        int gioDat = current.getHour();

        // Tao ra danh sach ban trong booking ngay hom nay theo ca
        List<Ban> tableInBookingDay = new ArrayList<>();

        for (BanDat bd : banDatRepository.findAll()) {

            // Tao Array luu ten cac ban duoc dat trong ca
            String[] tables = bd.getTablesBooking();

            // Lay ra gio dat cua booking
            LocalTime gioBanDaDat = LocalTime.parse((bd.getGioDat()));
            int checkGioDatCuaBan = gioBanDaDat.getHour();

            // Kiem tra xem booking co phai ngay hom nay khong
            if (Objects.equals(today, bd.getNgayDat()) && Objects.equals(bd.getStatus(), "Waiting")) {

                // Kiem tra xem booking va thoi gian hien tai co cung 1 ca lam viec khong
                if(gioDat >= 10 && gioDat < 14) {
                    if (checkGioDatCuaBan >= 10 && checkGioDatCuaBan < 14){

                        // Luu cac ban trong booking vao danh sach ban da duoc booking ngay hom nay
                        for (int i = 0; i < tables.length; i++) {
                            for (Ban ban : banRepository.findAll()) {
                                if (Objects.equals(tables[i], ban.getName())) {

                                    // Luu ban da duoc dat vao list
                                    tableInBookingDay.add(ban);
                                    break;
                                }
                            }
                        }
                    }
                } else if (gioDat >= 16 && gioDat < 22) {
                    if (checkGioDatCuaBan >= 16 && checkGioDatCuaBan < 22){

                        // Luu cac ban trong booking vao danh sach ban da duoc booking ngay hom nay
                        for (int i = 0; i < tables.length; i++) {
                            for (Ban ban : banRepository.findAll()) {
                                if (Objects.equals(tables[i], ban.getName())) {

                                    // Luu ban da duoc dat vao list
                                    tableInBookingDay.add(ban);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        for (Ban tableEmpty : banRepository.findAll()) {

            // Kiem tra xem ban nao dang trong
            if (tableEmpty.getTrangThai() == 1) {

                // Them ban trong vao danh sach ban trong
                tablesEmptyList.add(tableEmpty);

                // Lay ra so luong cho trong theo ban trong
                freeSlot = freeSlot + tableEmpty.getLoaiBan();
            }
        }

        // Kiem tra xem trong danh sach ban trong co ban nao duoc dat ngay hom nay theo ca chua
        for (Ban bookingTable : tableInBookingDay) {
            for (Ban banTrong : tablesEmptyList) {
                if (Objects.equals(bookingTable.getId(), banTrong.getId())) {

                    // Xoa bo ban da duoc dat ra khoi danh sach ban trong
                    tablesEmptyList.remove(banTrong);

                    // Giam so luong cho trong
                    freeSlot = freeSlot - banTrong.getLoaiBan();

                    break;
                }
            }
        }

        model.addAttribute("banDat",banDat);
        model.addAttribute("freeSlot",freeSlot);
        model.addAttribute("tablesEmptyList",tablesEmptyList);
        model.addAttribute("tablesList",banRepository.findAll());
        return "admin_templates/staff";
    }


    @GetMapping("booking")
    public String booking(Model model) {
        return "admin_templates/booking_index";
    }
}
