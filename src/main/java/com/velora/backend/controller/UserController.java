package com.velora.backend.controller;

import com.velora.backend.entity.User;
import com.velora.backend.dto.ForgotPasswordRequest;
import com.velora.backend.dto.LoginRequest;
import com.velora.backend.dto.VerifyOtpRequest;
import com.velora.backend.dto.ResetPasswordRequest;
import com.velora.backend.service.ForgotPasswordService;
import com.velora.backend.service.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ForgotPasswordService forgotPasswordService;


    // =========================
    // REGISTER
    // =========================

    @PostMapping("/register")
    public User saveUser(
            @Valid @RequestBody User user) {

        return userService.saveUser(user);
    }


    // =========================
    // GET ALL USERS
    // =========================

    @GetMapping
    public List<User> getAllUsers() {

        return userService.getAllUsers();
    }


    // =========================
    // GET USER BY ID
    // =========================

    @GetMapping("/{id}")
    public User getUserById(
            @PathVariable Integer id) {

        return userService.getUserById(id);
    }


    // =========================
    // UPDATE USER BY ID
    // =========================

    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable Integer id,
            @RequestBody User updatedUser) {

        return userService.updateUser(
                id,
                updatedUser
        );
    }


    // =========================
    // UPDATE INCOME
    // =========================

    @PutMapping("/income")
    public User updateIncome(
            @RequestParam Double income,
            Authentication authentication) {

        String email =
                authentication.getName();

        return userService.updateIncome(
                email,
                income
        );
    }


    // =========================
    // DELETE USER BY ID
    // =========================

    @DeleteMapping("/{id}")
    public String deleteUser(
            @PathVariable Integer id) {

        userService.deleteUser(id);

        return "User deleted successfully!";
    }


    // =========================
    // LOGIN
    // =========================

    @PostMapping("/login")
    public String loginUser(
            @Valid
            @RequestBody
            LoginRequest loginRequest) {

        return userService.loginUser(
                loginRequest
        );
    }
    @PostMapping("/verify-login-otp")
    public String verifyLoginOtp(
            @RequestBody VerifyOtpRequest request) {

        return userService.verifyLoginOtp(
                request.getEmail(),
                request.getOtp()
        );
    }


    // =========================
    // FORGOT PASSWORD
    // =========================

    @PostMapping("/forgot-password")
    public String forgotPassword(
            @RequestBody
            ForgotPasswordRequest request) {

        return forgotPasswordService
                .sendOtp(request);
    }


    // =========================
    // VERIFY OTP
    // =========================

    @PostMapping("/verify-otp")
    public String verifyOtp(
            @RequestBody
            VerifyOtpRequest request) {

        return forgotPasswordService.verifyOtp(
                request.getEmail(),
                request.getOtp()
        );
    }


    // =========================
    // RESET PASSWORD
    // =========================

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestBody
            ResetPasswordRequest request) {

        return forgotPasswordService
                .resetPassword(request);
    }


    // =====================================================
    // CURRENT LOGGED-IN USER
    // =====================================================

    @GetMapping("/me")
    public User getCurrentUser(
            Authentication authentication) {

        String email =
                authentication.getName();

        return userService
                .getCurrentUser(email);
    }


    // =====================================================
    // UPDATE CURRENT USER PROFILE
    // =====================================================

    @PutMapping("/me")
    public User updateCurrentUser(
            Authentication authentication,
            @RequestBody User updatedUser) {

        String email =
                authentication.getName();

        return userService.updateCurrentUser(
                email,
                updatedUser
        );
    }


    // =====================================================
    // CHANGE PASSWORD
    // =====================================================

    @PutMapping("/me/password")
    public String changePassword(
            Authentication authentication,
            @RequestBody Map<String, String> request) {

        String email =
                authentication.getName();

        String currentPassword =
                request.get("currentPassword");

        String newPassword =
                request.get("newPassword");

        if (currentPassword == null
                || currentPassword.isBlank()) {

            return "Current password is required";
        }

        if (newPassword == null
                || newPassword.isBlank()) {

            return "New password is required";
        }

        return userService.changePassword(
                email,
                currentPassword,
                newPassword
        );
    }


    // =====================================================
    // DELETE CURRENT ACCOUNT
    // =====================================================

    @DeleteMapping("/me")
    public String deleteCurrentUser(
            Authentication authentication) {

        String email =
                authentication.getName();

        return userService
                .deleteCurrentUser(email);
    }
}