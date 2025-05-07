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
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private List<UserEntity> users = new ArrayList<>();
    {
        users.add(new UserEntity("1", "firstName1", "lastName1", "1","+380961345725"));
        users.add(new UserEntity("2", "firstName2", "lastName2", "2","+380961345725"));
        users.add(new UserEntity("3", "firstName3", "lastName3", "3","+380961345725"));
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
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with ID " + id + " not found"));
    }


    public UserEntity create(UserEntity user) {
        return userRepository.save(user);
    }

    public UserEntity create(UserCreateRequest request) {

        if (userRepository.existsByCode(request.code())){
            throw new IllegalArgumentException("Cannot create user with duplicate code");
        }

        if (request.firstName() == null || request.firstName().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        } else if (request.lastName() == null || request.lastName().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        } else if (request.number() == null || !request.number().matches("^\\+380\\d{9}$")) {
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
        } else if (request.lastName() == null || request.lastName().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        } else if (request.number() == null || !request.number().matches("^\\+380\\d{9}$")) {
            throw new IllegalArgumentException("Invalid phone number");
        }

        UserEntity userPersisted = userRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("User with ID " + request.id() + " not found"));

        if (userPersisted != null) {
            if (!userPersisted.getCode().equals(request.code()) && userRepository.existsByCode(request.code())) {
                throw new IllegalArgumentException("Cannot update user with duplicate code");
            }

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
                    .code(request.code())
                    .build();

            return userRepository.save(userToUpdate);
        }
        return null;
    }


    public void delById(String id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User with ID " + id + " not found");
        }
        userRepository.deleteById(id);
    }


    private UserEntity mapToUser(UserCreateRequest request) {
        return new UserEntity(request.firstName(), request.lastName(), request.number(), request.code(), request.description());
    }
}
