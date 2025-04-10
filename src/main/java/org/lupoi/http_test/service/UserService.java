package org.lupoi.http_test.service;/*
    @author user
    @project HTTP_Test
    @class UserService
    @version 1.0.0
    @since 10.04.2025 - 12.59
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.http_test.model.User;
import org.lupoi.http_test.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private List<User> users = new ArrayList<>();
    {
        users.add(new User("1", "firstName1", "lastName1","+380123456789"));
        users.add(new User("2", "firstName2", "lastName2","+380987654321"));
        users.add(new User("3", "firstName3", "lastName3","+380134275869"));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public  User update(User user) {
        return userRepository.save(user);
    }

    public void delById(String id) {
        userRepository.deleteById(id);
    }
}
