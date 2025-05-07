package org.lupoi.http_test.service;
/*
    @author user
    @project HTTP_Test
    @class UserServiceMockTest
    @version 1.0.0
    @since 05.05.2025 - 16.09
*/

import org.lupoi.http_test.model.UserEntity;
import org.lupoi.http_test.repository.UserRepository;
import org.lupoi.http_test.request.UserCreateRequest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.lupoi.http_test.request.UserUpdateRequest;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
public class UserServiceMockTest {

    @Mock
    private UserRepository mockRepository;

    private UserService underTest;

    @Captor
    private ArgumentCaptor<UserEntity> argumentCaptor;

    private UserCreateRequest request;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new UserService(mockRepository);
    }

    @AfterEach
    void tearDown() {


    }


    @DisplayName("Create new Item. Happy Path")
    @Test
    void whenInsertNewItemAndCodeNotExistsThenOk() {
        //given
        request = new UserCreateRequest("Till", "Lindemann", "+380648478564", "Rammstein","poet");
        user = UserEntity.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .code(request.code())
                .description(request.description())
                .build();
        given(mockRepository.existsByCode(request.code())).willReturn(false);
        // when
        underTest.create(request);
        // then
        then(mockRepository).should().save(argumentCaptor.capture());
        UserEntity itemToSave = argumentCaptor.getValue();
        assertThat(itemToSave.getFirstName()).isEqualTo(request.firstName());
        assertNotNull(itemToSave.getCreateDate());
        assertTrue(itemToSave.getCreateDate().isBefore(LocalDateTime.now()));
        assertTrue(itemToSave.getUpdateDate().isEmpty());
    }

    @DisplayName("Verify repository methods were called correctly")
    @Test
    void whenCreateCalledThenRepositoryMethodsCalledOnce() {
        // given
        request = new UserCreateRequest("Till", "Lindemann", "+380648478564", "Queen", "poet");
        user = UserEntity.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .code(request.code())
                .description(request.description())
                .build();
        given(mockRepository.existsByCode(request.code())).willReturn(false);

        // when
        underTest.create(request);

        // then
        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(mockRepository, times(1)).existsByCode(request.code());
        verify(mockRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getFirstName()).isEqualTo(request.firstName());

        UserEntity saved = captor.getValue();
        assertEquals(request.firstName(), saved.getFirstName());
    }

    @DisplayName("Should throw when code already exists")
    @Test
    void whenCodeAlreadyExistsThenThrowException() {
        // given
        request = new UserCreateRequest("Till", "Lindemann", "+380648478564", "Rammstein", "poet");
        given(mockRepository.existsByCode(request.code())).willReturn(true);

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> underTest.create(request)
        );
        assertEquals("Cannot create user with duplicate code", exception.getMessage());
        verify(mockRepository, times(1)).existsByCode(request.code());
        verify(mockRepository, never()).save(any());
    }

    @DisplayName("Update user. Happy Path")
    @Test
    void whenUpdateUserWithValidData_thenSuccess() {
        // given
        UserEntity existingUser = UserEntity.builder()
                        .id("1")
                        .firstName("firstName1")
                        .lastName("lastName1")
                        .number("+380961345725")
                        .code("Uptade")
                        .createDate(LocalDateTime.now().minusDays(1))
                        .updateDate(new ArrayList<>())
                        .build();
        UserUpdateRequest request = new UserUpdateRequest(
                "1", "UpdatedName", "UpdatedLast", "+380961111111", "Uptade", "Updated description"
        );



        given(mockRepository.findById("1")).willReturn(java.util.Optional.of(existingUser));
        given(mockRepository.save(any(UserEntity.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        UserEntity result = underTest.update(request);

        // then
        assertNotNull(result);
        assertEquals("UpdatedName", result.getFirstName());
        assertEquals("UpdatedLast", result.getLastName());
        assertEquals("Uptade", result.getCode());
        assertEquals("Updated description", result.getDescription());
        assertEquals(1, result.getUpdateDate().size());
    }

    @DisplayName("Update user with duplicate code should fail")
    @Test
    void whenUpdateUserWithDuplicateCode_thenThrowException() {
        // given
        UserUpdateRequest request = new UserUpdateRequest(
                "1", "NewName", "NewLast", "+380961111111", "DUPLICATE", "desc"
        );

        UserEntity userInDb = UserEntity.builder()
                .id("1")
                .firstName("OldName")
                .lastName("OldLast")
                .number("+380961345725")
                .code("ORIGINAL")
                .createDate(LocalDateTime.now().minusDays(2))
                .updateDate(new ArrayList<>())
                .build();

        given(mockRepository.findById("1")).willReturn(java.util.Optional.of(userInDb));
        given(mockRepository.existsByCode("DUPLICATE")).willReturn(true);

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.update(request));

        // then
        assertEquals("Cannot update user with duplicate code", exception.getMessage());
        verify(mockRepository, never()).save(any());
    }

    @DisplayName("Update fails when user not found")
    @Test
    void whenUpdateCalledWithNonExistentId_thenReturnNull() {
        // given
        UserUpdateRequest request = new UserUpdateRequest(
                "999", "Test", "Test", "+380961234567", "AnyCode", "desc"
        );

        given(mockRepository.findById("999")).willReturn(java.util.Optional.empty());

        // when
        UserEntity result = underTest.update(request);

        // then
        assertNull(result);
        verify(mockRepository, never()).save(any());
    }

    @DisplayName("Update fails when first name is empty")
    @Test
    void whenUpdateWithEmptyFirstName_thenThrow() {
        // given
        UserUpdateRequest request = new UserUpdateRequest(
                "1", "", "Last", "+380961111111", "Code", "desc"
        );

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.update(request));
        assertEquals("First name cannot be empty", exception.getMessage());
    }

    @DisplayName("Update fails when last name is empty")
    @Test
    void whenUpdateWithEmptyLastName_thenThrow() {
        // given
        UserUpdateRequest request = new UserUpdateRequest(
                "1", "First", "", "+380961111111", "Code", "desc"
        );

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.update(request));
        assertEquals("Last name cannot be empty", exception.getMessage());
    }

    @DisplayName("Update fails with invalid phone number format")
    @Test
    void whenUpdateWithInvalidPhone_thenThrow() {
        // given
        UserUpdateRequest request = new UserUpdateRequest(
                "1", "First", "Last", "123456", "Code", "desc"
        );

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> underTest.update(request));
        assertEquals("Invalid phone number", exception.getMessage());
    }

    @DisplayName("Update appends a new timestamp to updateDate list")
    @Test
    void whenUpdateCalled_thenUpdateDateListGrows() {
        // given
        ArrayList<LocalDateTime> existingUpdates = new ArrayList<>();
        existingUpdates.add(LocalDateTime.now().minusDays(2));

        UserEntity userInDb = UserEntity.builder()
                .id("1")
                .firstName("Old")
                .lastName("Old")
                .number("+380961345725")
                .code("Code")
                .createDate(LocalDateTime.now().minusDays(3))
                .updateDate(existingUpdates)
                .build();

        UserUpdateRequest request = new UserUpdateRequest(
                "1", "New", "New", "+380961111111", "Code", "New description"
        );

        given(mockRepository.findById("1")).willReturn(java.util.Optional.of(userInDb));
        given(mockRepository.save(any(UserEntity.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        UserEntity result = underTest.update(request);

        // then
        assertEquals(2, result.getUpdateDate().size());
    }

}
