package org.lupoi.http_test.service;/*
    @author user
    @project HTTP_Test
    @class UserService
    @version 1.0.0
    @since 10.04.2025 - 12.59
*/

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.lupoi.http_test.model.UserEntity;
import org.lupoi.http_test.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final List<UserEntity> users = new ArrayList<>();
    {
        users.add(new UserEntity("1", "firstName1", "lastName1","+380123456789"));
        users.add(new UserEntity("2", "firstName2", "lastName2","+380987654321"));
        users.add(new UserEntity("3", "firstName3", "lastName3","+380134275869"));
    }
    @PostConstruct
    void init() {
        userRepository.deleteAll();
        userRepository.saveAll(users);

    }

    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }

    public UserEntity getById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    public UserEntity create(UserEntity user) {
        return userRepository.save(user);
    }

    public UserEntity update(UserEntity user) {
        return userRepository.save(user);
    }

    public void delById(String id) {
        userRepository.deleteById(id);
    }
}
