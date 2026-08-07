package com.velora.backend.controller;

import com.velora.backend.entity.User;
import java.util.List;
import com.velora.backend.dto.ForgotPasswordRequest;
import com.velora.backend.service.ForgotPasswordService;
import com.velora.backend.dto.LoginRequest;
import com.velora.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.velora.backend.dto.VerifyOtpRequest;
import com.velora.backend.dto.ResetPasswordRequest;
import jakarta.validation.Valid;

@RestController     //Makes this class a REST API controller
@RequestMapping("/api/users")  //Every API in this class will start with /api/users.

public class UserController {


    @Autowired  //Injects the UserService.
    private UserService userService;

    @Autowired
    private ForgotPasswordService forgotPasswordService;


    @PostMapping("/register")
    public User saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Integer id, @RequestBody User updatedUser) {
        return userService.updateUser(id, updatedUser);
    }
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return "User deleted successfully!";
    }
    @PostMapping("/login")
    public String loginUser(@RequestBody LoginRequest loginRequest) {
        return userService.loginUser(loginRequest);
    }
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return forgotPasswordService.sendOtp(request);
    }
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestBody VerifyOtpRequest request) {

        return forgotPasswordService.verifyOtp(
                request.getEmail(),
                request.getOtp()
        );
    }
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordRequest request) {
        return forgotPasswordService.resetPassword(request);
    }
}