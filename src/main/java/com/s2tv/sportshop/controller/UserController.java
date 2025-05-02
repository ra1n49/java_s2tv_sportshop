package com.s2tv.sportshop.controller;


import com.s2tv.sportshop.model.Users;
import com.s2tv.sportshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public Users getUser(@PathVariable String userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/get_all_user")
    public List<Users> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/changePassword")
    public String changePassword(@RequestParam String email, @RequestParam String oldPassword, @RequestParam String newPassword) {
        return userService.changePassword(email, oldPassword, newPassword);
    }

    @PutMapping("/update/{userId}")
    public Users updateUser(@PathVariable String userId, @RequestBody Users userUpdateData) {
        return userService.updateUser(userId, userUpdateData);
    }
}
