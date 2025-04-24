package org.lupoi.http_test.controller;/*
    @author user
    @project HTTP_Test
    @class UserController
    @version 1.0.0
    @since 10.04.2025 - 12.58
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.http_test.model.UserEntity;
import org.lupoi.http_test.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users/")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @GetMapping
    public List<UserEntity> showAll() {
        return userService.getAll();
    }

    @GetMapping("{id}")
    public UserEntity showOneById(@PathVariable String id) {
        return userService.getById(id);
    }

    @PostMapping
    public UserEntity insert(@RequestBody UserEntity user) {
        return userService.create(user);
    }

    @PutMapping
    public UserEntity edit(@RequestBody UserEntity user) {
        return userService.update(user);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        userService.delById(id);
    }

}
