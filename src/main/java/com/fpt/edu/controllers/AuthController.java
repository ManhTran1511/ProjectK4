package com.fpt.edu.controllers;

import com.fpt.edu.models.Blog;
import com.fpt.edu.models.Gallery;
import com.fpt.edu.models.UserDto;
import com.fpt.edu.repository.UserRepository;
import com.fpt.edu.security.services.UserService;
import com.fpt.edu.security.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.fpt.edu.models.User;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/auth")
public class AuthController {
  private UserService userService;

  @Autowired
  UserRepository userRepository;

  private final StorageService storageService;

  @Autowired
  public AuthController(StorageService storageService, UserService userService) {
    this.storageService = storageService;
    this.userService = userService;
  }

  @RequestMapping(value = "")
  public String userManager(Model model) throws IOException {
    storageService.loadAll().map(
                    path -> MvcUriComponentsBuilder.fromMethodName(AuthController.class,
                            "serveFile", path.getFileName().toString()).build().toUri().toString())
            .collect(Collectors.toList());
    model.addAttribute("listUsers", userRepository.findAll());
    return "account_templates/avatar";
  }

  @GetMapping("/register")
  public String showRegistrationForm(Model model){
    UserDto user = new UserDto();
    model.addAttribute("user", user);
    return "/account_templates/register";
  }

  @GetMapping("/files/{filename:.+}")
  @ResponseBody
  public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

    Resource file = storageService.loadAsResource(filename);
    return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + file.getFilename() + "\"").body(file);
  }

  @PostMapping("/save")
  public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                             BindingResult result,
                             Model model){
    User existingUser = userService.findUserByEmail(userDto.getEmail());

    if(existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()){
      result.rejectValue("email", null,
              "There is already an account registered with the same email");
    }

    if(result.hasErrors()){
      model.addAttribute("user", userDto);
      return "/account_templates/register";
    }

    userService.saveUser(userDto);
    return "redirect:/api/auth/login?success";
  }

  @GetMapping("/users")
  public String users(Model model){
    List<UserDto> users = userService.findAllUsers();
    model.addAttribute("users", users);
    return "/account_templates/user";
  }

  @GetMapping("/login")
  public String login() {
    return "/account_templates/login";
  }

  @GetMapping("/avatar/{id}")
  public String showUpdateForm(@PathVariable("id") long id, Model model){
    User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid User Id:" + id));

    model.addAttribute("user", user);
    model.addAttribute("allUsers", userRepository.findAll());
    return "/account_templates/avatar";
  }

  @PostMapping("/update/{id}")
  public String update(@PathVariable("id") long id, @Valid User user,
                           BindingResult result, Model model, @RequestParam("file") MultipartFile file) {
    if (result.hasErrors()) {
      user.setId(id);
      return "account_templates/avatar";
    }
    storageService.store(file);

    user.setImage(file.getOriginalFilename());

    userRepository.save(user);
    return "redirect:/admin";
  }
}
