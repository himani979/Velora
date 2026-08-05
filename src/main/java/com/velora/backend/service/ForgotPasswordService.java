package com.velora.backend.service;

import com.velora.backend.dto.ForgotPasswordRequest;
import com.velora.backend.entity.User;
import com.velora.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.velora.backend.dto.ResetPasswordRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Service
public class ForgotPasswordService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public String sendOtp(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            return "User not found";
        }

        String otp = mailService.generateOtp();

        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

        userRepository.save(user);

        mailService.sendOtpMail(user.getEmail(), otp);

        return "OTP Sent Successfully";
    }
        public String verifyOtp(String email, String otp) {

            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                return "User not found";
            }

            if (user.getOtp() == null) {
                return "OTP not generated";
            }

            if (!user.getOtp().equals(otp)) {
                return "Invalid OTP";
            }

            if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
                return "OTP Expired";
            }

            return "OTP Verified Successfully";
        }
    public String resetPassword(ResetPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            return "User not found";
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        user.setOtp(null);
        user.setOtpExpiry(null);

        userRepository.save(user);

        return "Password Reset Successfully";
    }
    }
