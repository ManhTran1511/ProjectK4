package com.fpt.edu.controllers;

import com.fpt.edu.models.UserDto;
import com.fpt.edu.security.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.fpt.edu.models.User;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/api/auth")
public class AuthController {
  private UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/index")
  public String home(){
    return "index";
  }

  @GetMapping("/register")
  public String showRegistrationForm(Model model){
    UserDto user = new UserDto();
    model.addAttribute("user", user);
    return "/account_templates/register";
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
    return "redirect:/api/auth/register?success";
  }

  @GetMapping("/users")
  public String users(Model model){
    List<UserDto> users = userService.findAllUsers();
    model.addAttribute("users", users);
    return "/account_templates/user";
  }

  @GetMapping("/login")
  public String login(){
    return "/account_templates/login";
  }
}
