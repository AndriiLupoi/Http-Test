package org.lupoi.http_test.controller;/*
    @author user
    @project HTTP_Test
    @class UserController
    @version 1.0.0
    @since 10.04.2025 - 12.58
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.http_test.model.UserEntity;
import org.lupoi.http_test.request.UserCreateRequest;
import org.lupoi.http_test.request.UserUpdateRequest;
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

    //============== request =====================
    @PostMapping("/dto")
    public UserEntity insert(@RequestBody UserCreateRequest request) {
        return userService.create(request);
    }

    @PutMapping
    public UserEntity edit(@RequestBody UserEntity user) {
        return userService.update(user);
    }

    //============== request =====================
    @PutMapping("/dto")
    public UserEntity edit(@RequestBody UserUpdateRequest request) {
        return userService.update(request);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        userService.delById(id);
    }

}
