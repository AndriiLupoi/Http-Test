package org.lupoi.http_test.service;/*
    @author user
    @project HTTP_Test
    @class UserServiceTest
    @version 1.0.0
    @since 30.04.2025 - 22.08
*/


import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lupoi.http_test.model.UserEntity;
import org.lupoi.http_test.repository.UserRepository;
import org.lupoi.http_test.request.UserCreateRequest;
import org.lupoi.http_test.request.UserUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
    }
    //  @AfterEach
    void tearsDown(){
        userRepository.deleteAll();
    }

    @Test
    void whenInsertNewUser_ThenCreateDateIsPresent() {
        //given
        UserCreateRequest request = new UserCreateRequest("Till","Lindemman", "+380961345725","26", "poet");
        LocalDateTime now = LocalDateTime.now();
        // when
        UserEntity createdUser = userService.create(request);
        // then
        assertNotNull(createdUser);
        assertNotNull(createdUser.getId());
        assertEquals("Till", createdUser.getFirstName());
        assertEquals("Lindemman", createdUser.getLastName());
        assertEquals("+380961345725", createdUser.getNumber());
        assertEquals("poet", createdUser.getDescription());
        assertNotNull(createdUser.getCreateDate());
        assertSame(LocalDateTime.class, createdUser.getCreateDate().getClass());
//        assertFalse(createdUser.getCreateDate().isEqual(now));
        assertNotNull(createdUser.getUpdateDate());
        assertSame(ArrayList.class, createdUser.getUpdateDate().getClass());
        assertTrue(createdUser.getUpdateDate().isEmpty());

    }


    @Test
    void whenCreateUserWithNullDescription_ThenDescriptionShouldBeNull() {
        // given
        UserCreateRequest request = new UserCreateRequest("John", "Doe", "+380961345725","25", null);
        // when
        UserEntity createdUser = userService.create(request);
        // then
        assertNotNull(createdUser);
        assertNull(createdUser.getDescription());
    }

    @Test
    void whenUpdateUser_ThenUpdateDateIsUpdated() {
        // given
        UserEntity user = new UserEntity("Alice", "Wonder", "+380987654321","24", "developer");
        userRepository.save(user);

        UserUpdateRequest request = new UserUpdateRequest(user.getId(), "Alice", "Wonderland", "+380987654322","23", "developer");

        // when
        UserEntity updatedUser = userService.update(request);

        // then
        assertNotNull(updatedUser);
        assertEquals("Alice", updatedUser.getFirstName());
        assertEquals("Wonderland", updatedUser.getLastName());
        assertEquals("+380987654322", updatedUser.getNumber());
        assertEquals("developer", updatedUser.getDescription());

        assertNotNull(updatedUser.getUpdateDate());
        assertFalse(updatedUser.getUpdateDate().isEmpty());
    }

    @Test
    void whenGetUserById_ThenUserReturned() {
        // given
        UserEntity user = new UserEntity("Bob", "Marley", "+380134275869","22", "musician");
        userRepository.save(user);
        // when
        UserEntity foundUser = userService.getById(user.getId());
        // then
        assertNotNull(foundUser);
        assertEquals("Bob", foundUser.getFirstName());
        assertEquals("Marley", foundUser.getLastName());
        assertEquals("+380134275869", foundUser.getNumber());
    }

    @Test
    void whenGetUserByInvalidId_ThenReturnNull() {
        // when
        UserEntity foundUser = userService.getById("invalid-id");
        // then
        assertNull(foundUser);
    }

    @Test
    void whenDeleteUser_ThenUserIsDeleted() {
        // given
        UserEntity user = new UserEntity("Charlie", "Brown", "+380987654320","21", "artist");
        userRepository.save(user);
        // when
        userService.delById(user.getId());
        // then
        assertNull(userRepository.findById(user.getId()).orElse(null));
    }

    @Test
    void whenDeleteUserWithInvalidId_ThenNothingHappens() {
        // when
        userService.delById("invalid-id");

        // then
        assertEquals(10, userRepository.count());
    }

    @Test
    void whenUpdateNonExistentUser_ThenReturnNull() {
        // given
        UserUpdateRequest updateRequest = new UserUpdateRequest("non-existing-id", "Updated", "Name", "+380961345725", "20","artist");
        // when
        UserEntity updatedUser = userService.update(updateRequest);
        // then
        assertNull(updatedUser);
    }

    @Test
    void whenCreateUserWithNullFirstName_ThenThrowError() {
        // given
        UserCreateRequest request = new UserCreateRequest(null, "LastName", "+380961345725", "19","developer");
        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.create(request));
    }

    @Test
    void whenUpdateUserWithNullLastName_ThenThrowError() {
        // given
        UserUpdateRequest updateRequest = new UserUpdateRequest("existing-id", "Updated", null, "+380961345725","18", "artist");
        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.update(updateRequest));
    }

    @Test
    void whenCreateUserWithNullNumber_ThenThrowError() {
        // given
        UserCreateRequest request = new UserCreateRequest("First", "Last", null, "17","developer");
        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.create(request));
    }

    @Test
    void whenUpdateUserWithInvalidPhoneNumber_ThenThrowError() {
        // given
        UserUpdateRequest updateRequest = new UserUpdateRequest("existing-id", "Updated", "Name", "invalid-number","16", "artist");
        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.update(updateRequest));
    }

    @Test
    void whenInsertNewUser_ThenUserExistsInRepository() {
        // given
        UserCreateRequest request = new UserCreateRequest("John", "Doe", "+380961345725", "15","developer");
        // when
        UserEntity createdUser = userService.create(request);
        // then
        assertNotNull(createdUser);
        assertTrue(userRepository.existsById(createdUser.getId()));
    }

    @Test
    void whenInsertMultipleUsers_ThenAllUsersAreSaved() {
        // given
        UserCreateRequest request1 = new UserCreateRequest("Jane", "Doe", "+380961345725", "14","designer");
        UserCreateRequest request2 = new UserCreateRequest("Tom", "Smith", "+380961345725", "13","developer");
        // when
        userService.create(request1);
        userService.create(request2);
        // then
        assertEquals(8, userRepository.count());
    }

    @Test
    void whenUserIsDeleted_ThenOtherUsersRemainIntact() {
        // given
        UserEntity user = new UserEntity("Samuel", "Jackson", "+380961345725","12", "actor");
        userRepository.save(user);
        // when
        userService.delById(user.getId());
        // then
        assertEquals(13, userRepository.count());
    }

    @Test
    void whenCreateUserWithInvalidPhoneNumber_ThenThrowError() {
        // given
        UserCreateRequest request = new UserCreateRequest("Invalid", "User", "123456","11", "developer");
        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.create(request));
    }

    @Test
    void whenUpdateUser_ThenUserExistsInRepository() {
        // given
        UserEntity user = new UserEntity("Alice", "Wonder", "+380987654321","10", "developer");
        userRepository.save(user);

        UserUpdateRequest updateRequest = new UserUpdateRequest(user.getId(), "Alice", "Wonderland", "+380987654322","9", "developer");
        // when
        userService.update(updateRequest);
        // then
        assertTrue(userRepository.existsById(user.getId()));
    }

    @Test
    void whenUpdateUserWithEmptyFirstName_ThenThrowError() {
        // given
        UserEntity user = new UserEntity("John", "Doe", "+380961345725","8", "developer");
        userRepository.save(user);

        UserUpdateRequest updateRequest = new UserUpdateRequest(user.getId(), "", "Doe", "+380961345725","7", "developer");
        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.update(updateRequest));
    }

    @Test
    void whenCreateUserWithValidPhoneNumber_ThenUserIsCreated() {
        // given
        UserCreateRequest request = new UserCreateRequest("James", "Bond", "+380961234567","6", "spy");
        // when
        UserEntity createdUser = userService.create(request);
        // then
        assertNotNull(createdUser);
        assertEquals("+380961234567", createdUser.getNumber());
    }

    @Test
    void whenUpdateUserWithEmptyPhoneNumber_ThenThrowError() {
        // given
        UserEntity user = new UserEntity("James", "Bond", "+380961234567","5", "spy");
        userRepository.save(user);

        UserUpdateRequest updateRequest = new UserUpdateRequest(user.getId(), "James", "Bond", "", "4","spy");
        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.update(updateRequest));


        userRepository.delete(user);
    }


}
