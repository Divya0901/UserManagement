package com.dg.UserManagementSystem.Controller;

import com.dg.UserManagementSystem.DTO.UserDetailsDTO;
import com.dg.UserManagementSystem.DTO.UserLoginDetailsDTO;
import com.dg.UserManagementSystem.Entity.User;
import com.dg.UserManagementSystem.Entity.PasswordResetToken;
import com.dg.UserManagementSystem.Repository.TokenRepository;
import com.dg.UserManagementSystem.Repository.UserDetailsRepository;
import com.dg.UserManagementSystem.Service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController

public class UserRegistrationController {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    UserDetailsRepository userDetailsRepository;

    @Autowired
    TokenRepository tokenRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "registration";
    }

    @PostMapping("/register")
    public String saveUser(@ModelAttribute UserDetailsDTO userDetailsDTO) {
        User userDetails = userDetailsService.save(userDetailsDTO);
        if (userDetails != null)
            return "redirect:/login";
        else
            return "redirect:/register";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public void login(@ModelAttribute UserLoginDetailsDTO userLoginDetailsDTO, Model model) {
        userDetailsService.loadUserByUsername(userLoginDetailsDTO.getUsername());
    }

    @GetMapping("/userDashboard")
    public String showUserDashboardForm() {
        return "userDashboard";
    }

    @GetMapping("/forgotPassword")
    public String forgotPassword() {
        return "forgotPassword";
    }

    @PostMapping("/forgotPassword")
    public String forgotPassordProcess(@ModelAttribute UserDetailsDTO userDetailsDTO) {
        String output = "";
        User user = userDetailsRepository.findByEmail(userDetailsDTO.getEmail());
        if (user != null) {
            output = userDetailsService.sendEmail(user);
        }
        if (output.equals("success")) {
            return "redirect:/forgotPassword?success";
        }
        return "redirect:/login?error";
    }

    @GetMapping("/resetPassword/{token}")
    public String resetPasswordForm(@PathVariable String token, Model model) {
        PasswordResetToken reset = tokenRepository.findByToken(token);
        if (reset != null && userDetailsService.hasExipred(reset.getExpiryDateTime())) {
            model.addAttribute("email", reset.getUser().getEmail());
            return "resetPassword";
        }
        return "redirect:/forgotPassword?error";
    }

    @PostMapping("/resetPassword")
    public String passwordResetProcess(@ModelAttribute UserDetailsDTO userDetailsDTO) {
        User user = userDetailsRepository.findByEmail(userDetailsDTO.getEmail());
        if(user != null) {
            user.setPassword(passwordEncoder.encode(userDetailsDTO.getPassword()));
            userDetailsRepository.save(user);
        }
        return "redirect:/login";
    }
}
