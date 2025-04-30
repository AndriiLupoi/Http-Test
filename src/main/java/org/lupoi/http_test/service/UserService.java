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
import org.lupoi.http_test.request.UserCreateRequest;
import org.lupoi.http_test.request.UserUpdateRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final List<UserEntity> users = new ArrayList<>();
    {
        users.add(new UserEntity("1", "firstName1", "lastName1","+380961345725"));
        users.add(new UserEntity("2", "firstName2", "lastName2","+380961345725"));
        users.add(new UserEntity("3", "firstName3", "lastName3","+380961345725"));
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

    public UserEntity create(UserCreateRequest request) {

        if (request.firstName() == null || request.firstName().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (request.lastName() == null || request.lastName().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (request.number() == null || !request.number().matches("^\\+380\\d{9}$")) {
            throw new IllegalArgumentException("Invalid phone number");
        }

        UserEntity user = mapToUser(request);
        user.setCreateDate(LocalDateTime.now());
        user.setUpdateDate(new ArrayList<>());
        return userRepository.save(user);
    }

    public UserEntity update(UserEntity user) {
        return userRepository.save(user);
    }

    public UserEntity update(UserUpdateRequest request) {
        if (request.firstName() == null || request.firstName().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (request.lastName() == null || request.lastName().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (request.number() == null || !request.number().matches("^\\+380\\d{9}$")) {
            throw new IllegalArgumentException("Invalid phone number");
        }

        UserEntity userPersisted = userRepository.findById(request.id()).orElse(null);
        if (userPersisted != null) {
            List<LocalDateTime> updateDates = userPersisted.getUpdateDate();

            // Ensure updateDates is not null before adding to it
            if (updateDates == null) {
                updateDates = new ArrayList<>();
            }

            updateDates.add(LocalDateTime.now());

            UserEntity userToUpdate = UserEntity.builder()
                    .id(request.id())
                    .firstName(request.firstName())
                    .lastName(request.lastName())
                    .number(request.number())
                    .description(request.description())
                    .createDate(userPersisted.getCreateDate())
                    .updateDate(updateDates)
                    .build();

            return userRepository.save(userToUpdate);
        }
        return null;
    }


    public void delById(String id) {
        userRepository.deleteById(id);
    }

    private UserEntity mapToUser(UserCreateRequest request) {
        return new UserEntity(request.firstName(), request.lastName(), request.number(), request.description());
    }
}
