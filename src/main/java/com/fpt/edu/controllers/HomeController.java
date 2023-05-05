package com.fpt.edu.controllers;

import com.fpt.edu.models.Blog;
import com.fpt.edu.models.Contact;
import com.fpt.edu.models.Contact_check;
import com.fpt.edu.models.Gallery;
import com.fpt.edu.repository.*;
import com.fpt.edu.security.storage.StorageFileNotFoundException;
import com.fpt.edu.security.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    private final StorageService storageService;

    @Autowired
    public HomeController(StorageService storageService) {
        this.storageService = storageService;
    }


    // USER ROUTES
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

        model.addAttribute("list3Blogs", list3Blogs);
        model.addAttribute("listContact", contactRepository.findAll());
        model.addAttribute("list3Galleries",list3Galleries );
        return "index";
    }

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
        return "user_templates/reservation";
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
            return "user_templates/contact";
        }
        contact_checkRepository.save(contact_check);
        return "redirect:/contact";
    }


    // ADMIN ROUTES
    @GetMapping("admin")
    public String admin(Model model) {
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
        return "admin_templates/staff";
    }

    @GetMapping("booking")
    public String booking(Model model) {
        return "admin_templates/booking_index";
    }

//    @GetMapping("staff/booking")
//    public String booking(Model model) {
//        return "admin_templates/booking_add_form";
//    }
}
