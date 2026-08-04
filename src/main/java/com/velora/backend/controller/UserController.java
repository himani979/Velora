package com.velora.backend.controller;

import com.velora.backend.entity.User;
import java.util.List;
import com.velora.backend.dto.LoginRequest;
import com.velora.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController     //Makes this class a REST API controller
@RequestMapping("/api/users")  //Every API in this class will start with /api/users.
public class UserController {

    @Autowired  //Injects the UserService.
    private UserService userService;
    @PostMapping
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
}