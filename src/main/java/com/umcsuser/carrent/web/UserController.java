package com.umcsuser.carrent.web;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.services.UserServiceInterface;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceInterface userService;

    public UserController(UserServiceInterface userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> list() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable String id) { return userService.findById(id); }
}
