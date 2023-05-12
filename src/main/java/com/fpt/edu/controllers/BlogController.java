package com.fpt.edu.controllers;

import com.fpt.edu.models.Ban;
import com.fpt.edu.models.Blog;
import com.fpt.edu.repository.BlogRepository;
import com.fpt.edu.security.services.BanService;
import com.fpt.edu.security.services.BlogService;
import com.fpt.edu.security.storage.StorageFileNotFoundException;
import com.fpt.edu.security.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping(value = "admin/blog")

public class BlogController {
    @Autowired
    BlogRepository blogRepository;

    @Autowired
    private BlogService blogService;

    private final StorageService storageService;

    @Autowired
    public BlogController(StorageService storageService) {
        this.storageService = storageService;
    }

    @ModelAttribute("allBlogs")
    public List<Blog> blogList() {
        return blogRepository.findAll();
    }


    @RequestMapping(value = "")
    public String blogManager(Model model, String keyword) throws IOException {
        storageService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(BlogController.class,
                                "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList());
        model.addAttribute("listBlogs", blogRepository.findAll());

        return getOnePage(model, 1, keyword);
//        return "admin_templates/blog_index";
    }

    @GetMapping("page/{pageNumber}")
    public String getOnePage(Model model, @PathVariable("pageNumber") int currentPage, String keyword) {
        Page<Blog> page;
        int totalPages;
        long totalItems;
        List<Blog> blogList;

        if (keyword == null || keyword.isEmpty()) {
            page = blogService.findPage(currentPage);
            totalPages = page.getTotalPages();
            totalItems = page.getTotalElements();
            blogList = page.getContent();
        } else {
            page = blogService.findByKeyword(keyword, currentPage);
            totalPages = page.getTotalPages();
            totalItems = page.getTotalElements();
            blogList = page.getContent();
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("blogList", blogList);


        return "admin_templates/blog_index";
    }

    @GetMapping("/page/{pageNumber}/{field}")
    public String getPageWithSort(Model model,
                                  @PathVariable("pageNumber") int currentPage,
                                  @PathVariable String field,
                                  @PathParam("sortDir") String sortDir){

        Page<Blog> page = blogService.findAllWithSort(field, sortDir, currentPage);
        List<Blog> blogList = page.getContent();
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();

        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);

        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc")?"desc":"asc");
        model.addAttribute("blogList", blogList);

        return "admin_templates/blog_index";
    }

    @RequestMapping(value = "/new")
    public String addForm(Model model) {
        Blog blog = new Blog();
        model.addAttribute("blog", blog);
        return "admin_templates/blog_add_form";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @RequestMapping(value = "/add")
    public String add(@Valid Blog blog,
                      BindingResult result, Model model,@RequestParam("file") MultipartFile file) {
        if (result.hasErrors()) {
            return "redirect:/admin/blog/new?error";
        }
        storageService.store(file);

        blog.setImage(file.getOriginalFilename());

        blogRepository.save(blog);
        return "redirect:/admin/blog?add";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Blog Id:" + id));

        model.addAttribute("blog", blog);
        model.addAttribute("allBlogs", blogRepository.findAll());
        return "admin_templates/blog_edit_form";
    }

    @PostMapping("/update/{id}")
    public String updateBlog(@PathVariable("id") long id, @Valid Blog blog,
                             BindingResult result, Model model,
                             @RequestParam("file") MultipartFile file) {
        if (result.hasErrors()) {
            blog.setId(id);
            return "redirect:/admin/blog/edit?error";
        }

        Blog oldBlog = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Blog Id:" + id));

        if (!file.isEmpty()){
            storageService.store(file);
            blog.setImage(file.getOriginalFilename());
        } else {
            blog.setImage(oldBlog.getImage());
        }

        blogRepository.save(blog);
        return "redirect:/admin/blog?edit";
    }

    @GetMapping("/delete/{id}")
    public String deleteBlog(@PathVariable("id") long id, Model model) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Blog Id:" + id));
        blogRepository.delete(blog);
        return "redirect:/admin/blog?delete";
    }

}
