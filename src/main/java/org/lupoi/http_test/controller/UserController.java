package org.lupoi.http_test.controller;/*
    @author user
    @project HTTP_Test
    @class UserController
    @version 1.0.0
    @since 10.04.2025 - 12.58
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.http_test.model.User;
import org.lupoi.http_test.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> showAll() {
        return userService.getAll();
    }

    @GetMapping("{id}")
    public User showOneById(@PathVariable String id) {
        return userService.getById(id);
    }

    @PostMapping
    public User insert(@RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User edit(@RequestBody User user) {
        return userService.update(user);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        userService.delById(id);
    }

}
