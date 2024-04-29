package org.mvasylchuk.userservice;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mvasylchuk.userservice.dto.CreateUserRequest;
import org.mvasylchuk.userservice.dto.UpdateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest
class UserControllerTest {
    @Autowired
    UserController underTest;
    @MockBean
    UserService userService;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(userService);
    }

    @Test
    void create() throws UserServiceException {
        CreateUserRequest request = new CreateUserRequest(
                "email@test.com",
                "firstName",
                "lastName",
                LocalDate.now().minusYears(20),
                "address",
                "+123456789098"
        );
        underTest.create(request);

        verify(userService).createUser(request);
    }

    @Test
    void updateComplete() throws UserServiceException {
        CreateUserRequest request = new CreateUserRequest(
                "email@test.com",
                "firstName",
                "lastName",
                LocalDate.now().minusYears(20),
                "address",
                "+123456789098"
        );
        underTest.updateComplete(1L, request);

        verify(userService).updateAllUserFields(1L, request);
    }

    @Test
    void updatePartial() throws UserServiceException {
        UpdateUserRequest request = new UpdateUserRequest("email@test.com",
                null,
                null,
                null,
                Optional.empty(),
                Optional.empty());
        underTest.updatePartial(1L, request);
        verify(userService).updateSomeUserFields(1L, request);
    }

    @Test
    void searchByBirthday() throws UserServiceException {
        underTest.searchByBirthday(LocalDate.of(2000,1,1),
                LocalDate.of(2024,1,1));
        verify(userService).searchUsersByBirthday(LocalDate.of(2000,1,1),
                LocalDate.of(2024,1,1));
    }

    @Test
    void delete() {
        underTest.delete(1L);
        verify(userService).deleteUser(1L);
    }
}