package com.velora.backend.service;

import com.velora.backend.entity.User;
import com.velora.backend.dto.LoginRequest;
import com.velora.backend.repository.UserRepository;
import com.velora.backend.security.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MailService mailService;


    // =========================
    // SAVE USER
    // =========================

    public User saveUser(User user) {

        // Check if email is already registered
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Account already exists. Please sign in.");
        }

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        return userRepository.save(user);
    }


    // =========================
    // GET ALL USERS
    // =========================

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    // =========================
    // GET USER BY ID
    // =========================

    public User getUserById(Integer id) {

        return userRepository
                .findById(id)
                .orElse(null);
    }


    // =========================
    // UPDATE USER
    // =========================

    public User updateUser(
            Integer id,
            User updatedUser) {

        User existingUser =
                userRepository
                        .findById(id)
                        .orElse(null);

        if (existingUser == null) {
            return null;
        }

        existingUser.setFullName(
                updatedUser.getFullName()
        );

        existingUser.setEmail(
                updatedUser.getEmail()
        );

        if (updatedUser.getPassword() != null
                && !updatedUser.getPassword().isBlank()) {

            existingUser.setPassword(
                    passwordEncoder.encode(
                            updatedUser.getPassword()
                    )
            );
        }

        existingUser.setPhoneNumber(
                updatedUser.getPhoneNumber()
        );

        existingUser.setProfileImage(
                updatedUser.getProfileImage()
        );

        return userRepository.save(existingUser);
    }


    // =========================
    // UPDATE INCOME
    // =========================

    public User updateIncome(
            String email,
            Double income) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {
            return null;
        }

        user.setIncome(income);

        return userRepository.save(user);
    }


    // =========================
    // DELETE USER
    // =========================

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }


    // =========================
    // LOGIN
    // =========================

    public String loginUser(
            LoginRequest loginRequest) {

        User user =
                userRepository
                        .findByEmail(
                                loginRequest.getEmail()
                        )
                        .orElse(null);

        if (user == null) {
            return "User not found!";
        }

        if (!passwordEncoder.matches(
                loginRequest.getPassword(),
                user.getPassword())) {

            return "Invalid Password!";
        }

        String otp = mailService.generateOtp();

        user.setOtp(otp);
        user.setOtpExpiry(
                java.time.LocalDateTime
                        .now()
                        .plusMinutes(5)
        );
        user.setOtpPurpose("LOGIN");
        user.setResetOtpVerified(false);
        userRepository.save(user);

        mailService.sendOtpMail(
                user.getEmail(),
                otp
        );

        return "OTP Sent Successfully";
    }

    public String verifyLoginOtp(
            String email,
            String otp) {

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            return "User not found!";
        }

        if (user.getOtp() == null) {
            return "OTP not generated";
        }
        if (!"LOGIN".equals(user.getOtpPurpose())) {
            return "Invalid OTP";
        }
        if (!user.getOtp().equals(otp)) {
            return "Invalid OTP";
        }

        if (user.getOtpExpiry() == null
                || user.getOtpExpiry()
                .isBefore(java.time.LocalDateTime.now())) {

            return "OTP Expired";
        }

        user.setOtp(null);
        user.setOtpExpiry(null);
        user.setOtpPurpose(null);
        user.setResetOtpVerified(false);
        userRepository.save(user);

        return jwtService.generateToken(
                user.getEmail()
        );
    }
    // =========================
// GET CURRENT USER
// =========================

    public User getCurrentUser(String email) {

        return userRepository
                .findByEmail(email)
                .orElse(null);
    }


// =========================
// UPDATE CURRENT USER
// =========================

    public User updateCurrentUser(
            String email,
            User updatedUser) {

        User existingUser =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (existingUser == null) {
            return null;
        }

        existingUser.setFullName(
                updatedUser.getFullName()
        );

        existingUser.setPhoneNumber(
                updatedUser.getPhoneNumber()
        );

        if (updatedUser.getProfileImage() != null) {
            existingUser.setProfileImage(
                    updatedUser.getProfileImage()
            );
        }

        return userRepository.save(existingUser);
    }


// =========================
// CHANGE PASSWORD
// =========================

    public String changePassword(
            String email,
            String currentPassword,
            String newPassword) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {
            return "User not found";
        }

        if (!passwordEncoder.matches(
                currentPassword,
                user.getPassword())) {

            return "Current password is incorrect";
        }

        user.setPassword(
                passwordEncoder.encode(
                        newPassword
                )
        );

        userRepository.save(user);

        return "Password changed successfully";
    }


// =========================
// DELETE CURRENT USER
// =========================

    public String deleteCurrentUser(String email) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {
            return "User not found";
        }

        userRepository.delete(user);

        return "Account deleted successfully";
    }
}