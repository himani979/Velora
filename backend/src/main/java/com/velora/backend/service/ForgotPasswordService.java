package com.velora.backend.service;

import com.velora.backend.dto.ForgotPasswordRequest;
import com.velora.backend.dto.ResetPasswordRequest;
import com.velora.backend.entity.User;
import com.velora.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ForgotPasswordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // =========================
    // SEND PASSWORD RESET OTP
    // =========================
    public String sendOtp(ForgotPasswordRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return "User not found";
        }

        String otp = mailService.generateOtp();

        user.setOtp(otp);
        user.setOtpExpiry(
                LocalDateTime.now().plusMinutes(5)
        );

        user.setOtpPurpose("PASSWORD_RESET");
        user.setResetOtpVerified(false);

        userRepository.save(user);

        mailService.sendOtpMail(
                user.getEmail(),
                otp
        );

        return "OTP Sent Successfully";
    }

    // =========================
    // VERIFY PASSWORD RESET OTP
    // =========================
    public String verifyOtp(String email, String otp) {

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return "User not found";
        }

        if (user.getOtp() == null) {
            return "OTP not generated";
        }

        if (!"PASSWORD_RESET".equals(user.getOtpPurpose())) {
            return "Invalid password reset request";
        }

        if (!user.getOtp().equals(otp)) {
            return "Invalid OTP";
        }

        if (
                user.getOtpExpiry() == null ||
                        user.getOtpExpiry().isBefore(LocalDateTime.now())
        ) {
            return "OTP Expired";
        }

        user.setResetOtpVerified(true);

        userRepository.save(user);

        return "OTP Verified Successfully";
    }

    // =========================
    // RESET PASSWORD
    // =========================
    public String resetPassword(
            ResetPasswordRequest request
    ) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return "User not found";
        }

        if (!Boolean.TRUE.equals(user.getResetOtpVerified())) {
            return "OTP verification required";
        }

        if (!"PASSWORD_RESET".equals(user.getOtpPurpose())) {
            return "Invalid password reset request";
        }

        if (
                user.getOtpExpiry() == null ||
                        user.getOtpExpiry().isBefore(LocalDateTime.now())
        ) {
            return "OTP Expired";
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        // Clear OTP data after successful reset
        user.setOtp(null);
        user.setOtpExpiry(null);
        user.setOtpPurpose(null);
        user.setResetOtpVerified(false);

        userRepository.save(user);

        return "Password Reset Successfully";
    }
}