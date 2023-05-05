package com.fpt.edu.controllers;

import com.fpt.edu.models.Gallery;
import com.fpt.edu.repository.GalleryRepository;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "admin/gallery")
public class GalleryController {
    @Autowired
    GalleryRepository galleryRepository;

    private final StorageService storageService;

    @Autowired
    public GalleryController(StorageService storageService) {
        this.storageService = storageService;
    }

    @ModelAttribute("allGalleries")
    public List<Gallery> galleryList() {
        return galleryRepository.findAll();
    }

    @RequestMapping(value = "")
    public String galleryManager(Model model) throws IOException{
        storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(GalleryController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList());
        model.addAttribute("listGalleries", galleryRepository.findAll());
        return "admin_templates/gallery_index";
    }

    @RequestMapping(value = "/new")
    public String addForm(Model model) throws IOException {
        Gallery gallery = new Gallery();
        model.addAttribute("gallery", gallery);
        return "admin_templates/gallery_add_form";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }


    @RequestMapping(value = "/add")
    public String add(@Valid Gallery gallery,
                      BindingResult result, Model model, @RequestParam("file") MultipartFile file) {
        if (result.hasErrors()) {
            return "admin_templates/gallery_add_form";
        }

        storageService.store(file);

        gallery.setImage(file.getOriginalFilename());
//        redirectAttributes.addFlashAttribute("message",
//                "You successfully uploaded " + file.getOriginalFilename() +  "!");

        galleryRepository.save(gallery);
        return "redirect:/admin/gallery";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Gallery gallery = galleryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Gallery Id:" + id));

        model.addAttribute("gallery", gallery);
        model.addAttribute("allGallerys", galleryRepository.findAll());
        return "admin_templates/gallery_edit_form";
    }

    @PostMapping("/update/{id}")
    public String updateBlog(@PathVariable("id") long id, @Valid Gallery gallery,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            gallery.setId(id);
            return "admin_templates/gallery_edit_form";
        }

        galleryRepository.save(gallery);
        return "redirect:/admin/gallery";
    }

    @GetMapping("/delete/{id}")
    public String deleteBlog(@PathVariable("id") long id, Model model) {
        Gallery gallery = galleryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Gallery Id:" + id));
        galleryRepository.delete(gallery);
        return "redirect:/admin/Gallery";
    }

}
