package com.velora.backend.service;

import com.velora.backend.entity.User;
import java.util.List;
import com.velora.backend.security.JwtService;
import com.velora.backend.dto.LoginRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.velora.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    //"Take this User object and save it in the users table."

    // Save User

    public User saveUser(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }
    // Get All Users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    // Get User By ID
    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }
    public User updateUser(Integer id, User updatedUser) {

        User existingUser = userRepository.findById(id).orElse(null);

        if (existingUser == null) {
            return null;
        }

        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setProfileImage(updatedUser.getProfileImage());
        //existingUser.setEmailVerified(updatedUser.getEmailVerified());

        return userRepository.save(existingUser);
    }
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    public String loginUser(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);

        if (user == null) {
            return "User not found!";
        }

        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return jwtService.generateToken(user.getEmail());


        }

        return "Invalid Password!";
    }
}