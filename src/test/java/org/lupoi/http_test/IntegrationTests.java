package org.lupoi.http_test;/*
    @author user
    @project HTTP_Test
    @class IntegrationTests
    @version 1.0.0
    @since 07.05.2025 - 11.46
*/

import org.lupoi.http_test.Utils.Utils;
import org.lupoi.http_test.request.UserUpdateRequest;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lupoi.http_test.model.UserEntity;
import org.lupoi.http_test.repository.UserRepository;
import org.lupoi.http_test.request.UserCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository repository;
    private final List<UserEntity> users = new ArrayList<>();

    @BeforeEach
    void setUp(){
        {
            users.add(new UserEntity("1", "firstName1", "+380961111111", "1","+380961345725"));
            users.add(new UserEntity("2", "firstName2", "+380961111112", "2","+380961345725"));
            users.add(new UserEntity("3", "firstName3", "+380961111113", "3","+380961345725"));
            repository.saveAll(users);
        }
    }

    @AfterEach
    void tearDown(){
        repository.deleteAll();
    }

    @Test
    void itShouldCreateUser() throws Exception {
        //given
        UserCreateRequest request = new UserCreateRequest(
                "Till",
                "Lindemann",
                "+380648478564",
                "Rammstein",
                "poet");

        //when
        ResultActions resultActions = mockMvc.perform(post("http://localhost:8080/api/v1/users/dto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.toJson(request)));
        UserEntity user = repository.findAll()
                .stream()
                .filter(it -> it.getCode().equals(request.code()))
                .findFirst().orElse(null);

        //then
        resultActions.andExpect(status().isOk());
        assertThat(repository.existsByCode(request.code())).isTrue();
        assertNotNull(user);
        assertNotNull(user.getId());
        assertThat(user.getId()).isNotEmpty();
        assertThat(user.getId().length()).isEqualTo(24);
        assertThat(user.getDescription()).isEqualTo(request.description());
        assertThat(user.getFirstName()).isEqualTo(request.firstName());
        assertThat(user.getLastName()).isEqualTo(request.lastName());
        assertThat(user.getCode()).isEqualTo(request.code());
        assertThat(user.getUpdateDate()).isEmpty();
        assertThat(user.getCreateDate()).isNotNull();
    }

    @Test
    void itShouldNotCreateUser_WhenCodeAlreadyExists() throws Exception {
        //given
        UserCreateRequest request = new UserCreateRequest(
                "Duplicate",
                "User",
                "+380123456789",
                "1", // Already used code
                "desc");

        //when
        ResultActions resultActions = mockMvc.perform(post("http://localhost:8080/api/v1/users/dto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.toJson(request)));

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void itShouldUpdateUser() throws Exception {
        //given
        UserEntity existing = repository.findAll().get(0);
        UserUpdateRequest updateRequest = new UserUpdateRequest(
                existing.getId(),
                "UpdatedName",
                "UpdatedLastName",
                "+380987654321",
                existing.getCode(),
                "updated description"
        );
        //when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/users/dto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.toJson(updateRequest)));

        //then
        resultActions.andExpect(status().isOk());
        UserEntity updated = repository.findById(existing.getId()).orElseThrow();
        assertThat(updated.getFirstName()).isEqualTo(updateRequest.firstName());
        assertThat(updated.getDescription()).isEqualTo(updateRequest.description());
    }

    @Test
    void itShouldNotUpdateUser_WhenIdInvalid() throws Exception {
        //given
        UserUpdateRequest updateRequest = new UserUpdateRequest(
                "1",
                "UpdatedName",
                "UpdatedLastName",
                "+380987654321",
                "nonexistentCode",
                "updated description"
        );
        //when
        ResultActions resultActions = mockMvc.perform(
                put("/api/v1/users/dto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Utils.toJson(updateRequest)));

        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void itShouldGetUserById() throws Exception {
        //given
        UserEntity user = repository.findAll().get(0);
        //when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void itShouldNotGetUserById_WhenIdInvalid() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/users/123121")
                        .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void itShouldDeleteUserById() throws Exception {
        //given
        UserEntity user = repository.findAll().get(0);
        //when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions.andExpect(status().isOk());
        assertThat(repository.findById(user.getId())).isEmpty();
    }

    @Test
    void itShouldNotDeleteUserById_WhenIdInvalid() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(
                delete("/api/v1/users/1231421")
                        .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void itShouldGetAllUsers() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(
                get("/api/v1/users/")
                        .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk());
        String response = resultActions.andReturn().getResponse().getContentAsString();
        assertThat(response).contains(users.get(0).getFirstName(), users.get(1).getFirstName());
    }


    @Test
    void itShouldNotCreateUser_WhenMissingRequiredFields() throws Exception {
        //given
        UserCreateRequest request = new UserCreateRequest(
                "", // First name missing
                "Lindemann",
                "+380648478564",
                "Rammstein",
                "poet");

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/users/dto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Utils.toJson(request)));

        //then
        resultActions.andExpect(status().isBadRequest());
    }
}
