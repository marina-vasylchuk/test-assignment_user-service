package org.mvasylchuk.userservice;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mvasylchuk.userservice.dto.CreateUserRequest;
import org.mvasylchuk.userservice.dto.UpdateUserRequest;
import org.mvasylchuk.userservice.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceApplicationTests {
    @Autowired
    private UserService underTest;
    @Autowired
    private UserRepository repository;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void createUser() throws UserServiceException {
        CreateUserRequest request = new CreateUserRequest(
                "email@test.com",
                "firstName",
                "lastName",
                LocalDate.now().minusYears(20),
                "address",
                "+123456789098"
        );

        UserDto actual = underTest.createUser(request);

        //Check response
        assertEquals("email@test.com", actual.getEmail());
        assertEquals("firstName", actual.getFirstName());
        assertEquals("lastName", actual.getLastName());
        assertEquals(LocalDate.now().minusYears(20), actual.getBirthDate());
        assertEquals("address", actual.getAddress());
        assertEquals("+123456789098", actual.getPhoneNumber());


        //Check actual DB data
        Optional<UserEntity> savedUserOpt = repository.findById(actual.getId());

        assertTrue(savedUserOpt.isPresent());

        UserEntity savedUser = savedUserOpt.get();
        assertEquals("email@test.com", savedUser.getEmail());
        assertEquals("firstName", savedUser.getFirstName());
        assertEquals("lastName", savedUser.getLastName());
        assertEquals(LocalDate.now().minusYears(20), savedUser.getBirthDate());
        assertEquals("address", savedUser.getAddress());
        assertEquals("+123456789098", savedUser.getPhoneNumber());
    }

    @Test
    void createUser_whenBirthDateIsLessThen18Ago_thenShouldThrowError() {
        CreateUserRequest request = new CreateUserRequest(
                "email@test.com",
                "firstName",
                "lastName",
                LocalDate.now().minusYears(2),
                "address",
                "+123456789098"
        );

        UserServiceException actual = assertThrows(UserServiceException.class, () -> underTest.createUser(request));
        Assertions.assertEquals("To register you have to be 18 years old", actual.getMessage());
    }

    @Test
    void createUser_whenUserWasAlreadyRegistered_thenShouldThrowError() {
        repository.save(new UserEntity(null, "email@test.com",
                "firstName",
                "lastName",
                LocalDate.now().minusYears(21),
                "address",
                "+123456789098"));

        CreateUserRequest request = new CreateUserRequest(
                "email@test.com",
                "firstName1",
                "lastName1",
                LocalDate.now().minusYears(21),
                "address1",
                "+123456789999"
        );
        UserServiceException actual = assertThrows(UserServiceException.class, () -> underTest.createUser(request));
        assertEquals("Users with provided email is exist", actual.getMessage());
    }

    @Test
    void updateAllUserFields() throws UserServiceException {
        UserEntity saved = repository.save(new UserEntity(null,
                "email22@test.com",
                "firstName",
                "lastName",
                LocalDate.now().minusYears(21),
                "address",
                "+123456789098"));

        //check response
        UserDto actual = underTest.updateAllUserFields(saved.getId(), new CreateUserRequest("email@test1.com",
                "firstName1",
                "lastName1",
                LocalDate.now().minusYears(22),
                "address1",
                "+123456777778"));

        assertEquals("email@test1.com", actual.getEmail());
        assertEquals("firstName1", actual.getFirstName());
        assertEquals("lastName1", actual.getLastName());
        assertEquals(LocalDate.now().minusYears(22), actual.getBirthDate());
        assertEquals("address1", actual.getAddress());
        assertEquals("+123456777778", actual.getPhoneNumber());

        Optional<UserEntity> savedUserOpt = repository.findById(saved.getId());

        assertTrue(savedUserOpt.isPresent());

        UserEntity savedUser = savedUserOpt.get();

        assertEquals("email@test1.com", savedUser.getEmail());
        assertEquals("firstName1", savedUser.getFirstName());
        assertEquals("lastName1", savedUser.getLastName());
        assertEquals(LocalDate.now().minusYears(22), savedUser.getBirthDate());
        assertEquals("address1", savedUser.getAddress());
        assertEquals("+123456777778", savedUser.getPhoneNumber());

    }

    @Test
    void updateAllUsersTest_whenBirthDateIsLessThen18Ago_thenShouldThrowError() {
        UserEntity saved = repository.save(new UserEntity(null,
                "email@test.com",
                "firstName",
                "lastName",
                LocalDate.now().minusYears(21),
                "address",
                "+123456789098"));

        CreateUserRequest request = new CreateUserRequest(
                "email@test.com",
                "firstName15",
                "lastName17",
                LocalDate.now().minusYears(14),
                "address1",
                "+123456789999"
        );
        UserServiceException actual = assertThrows(UserServiceException.class, () -> underTest.updateAllUserFields(saved.getId(), request));

        assertEquals("To register you have to be 18 years old", actual.getMessage());
    }

    @Test
    void updateAllUsersTest_whenUserIsAbsent_thenShouldThrowError() {
        CreateUserRequest request = new CreateUserRequest(
                "email@test.com",
                "firstName15",
                "lastName17",
                LocalDate.now().minusYears(22),
                "address1",
                "+123456789999"
        );

        UserServiceException actual = assertThrows(UserServiceException.class, () -> underTest.updateAllUserFields(10L, request));

        assertEquals("User is not found", actual.getMessage());
    }

    @Test
    void deleteUser() {
        UserEntity savedUser = repository.save(new UserEntity(null, "email1@test.com",
                "firstName",
                "lastName",
                LocalDate.now().minusYears(21),
                "address",
                "+123456789098"));

        underTest.deleteUser(savedUser.getId());

        assertTrue(repository.findById(savedUser.getId()).isEmpty());
    }

    @Test
    void deleteUser_whenUserDoesNotExist_thanDoesNotThrowAnyError() {

        underTest.deleteUser(2022L);

        assertTrue(repository.findById(2022L).isEmpty());
    }

    @Test
    void updateSomeUserFields() throws UserServiceException {
        UserEntity saved = repository.save(new UserEntity(null,
                "email22@test.com",
                "firstName",
                "lastName",
                LocalDate.now().minusYears(21),
                "address",
                "+123456789098"));

        //check response
        UserDto actual = underTest.updateSomeUserFields(saved.getId(), new UpdateUserRequest("email@test1.com",
                null,
                null,
                null,
                Optional.empty(),
                Optional.empty()));

        assertEquals("email@test1.com", actual.getEmail());
        assertEquals("firstName", actual.getFirstName());
        assertEquals("lastName", actual.getLastName());
        assertEquals(LocalDate.now().minusYears(21), actual.getBirthDate());
        assertEquals("address", actual.getAddress());
        assertEquals("+123456789098", actual.getPhoneNumber());

        Optional<UserEntity> updateUserOpt = repository.findById(actual.getId());
        assertTrue(updateUserOpt.isPresent());
        UserEntity updatedUser = updateUserOpt.get();

        assertEquals("email@test1.com", updatedUser.getEmail());
        assertEquals("firstName", updatedUser.getFirstName());
        assertEquals("lastName", updatedUser.getLastName());
        assertEquals(LocalDate.now().minusYears(21), updatedUser.getBirthDate());
        assertEquals("address", updatedUser.getAddress());
        assertEquals("+123456789098", updatedUser.getPhoneNumber());

    }

    @Test
    void updateSomeUserFields_whenUpdateAllFieldsExceptEmail() throws UserServiceException {
        UserEntity saved = repository.save(new UserEntity(null,
                "email@test.com",
                "firstName",
                "lastName",
                LocalDate.now().minusYears(21),
                "address",
                "+123456789098"));
        //check response
        UserDto actual = underTest.updateSomeUserFields(saved.getId(), new UpdateUserRequest(null,
                "firstName1",
                "lastName1",
                LocalDate.now().minusYears(23),
                Optional.of("address1"),
                Optional.of("+1111111111")));

        assertEquals("email@test.com", actual.getEmail());
        assertEquals("firstName1", actual.getFirstName());
        assertEquals("lastName1", actual.getLastName());
        assertEquals(LocalDate.now().minusYears(23), actual.getBirthDate());
        assertEquals("address1", actual.getAddress());
        assertEquals("+1111111111", actual.getPhoneNumber());

        Optional<UserEntity> updateUserOpt = repository.findById(actual.getId());
        assertTrue(updateUserOpt.isPresent());
        UserEntity updatedUser = updateUserOpt.get();

        assertEquals("email@test.com", updatedUser.getEmail());
        assertEquals("firstName1", updatedUser.getFirstName());
        assertEquals("lastName1", updatedUser.getLastName());
        assertEquals(LocalDate.now().minusYears(23), updatedUser.getBirthDate());
        assertEquals("address1", updatedUser.getAddress());
        assertEquals("+1111111111", updatedUser.getPhoneNumber());

    }

    @Test
    void updateSomeUserFields_whenBirthDateIsLessThen18Ago_thenShouldThrowError() {
        UserEntity saved = repository.save(new UserEntity(null,
                "email@test.com",
                "firstName",
                "lastName",
                LocalDate.now().minusYears(21),
                "address",
                "+123456789098"));

        UserServiceException actual = assertThrows(UserServiceException.class, () -> underTest.updateSomeUserFields(saved.getId(),
                new UpdateUserRequest(null,
                        null,
                        null,
                        LocalDate.now().minusYears(12),
                        Optional.empty(),
                        Optional.empty())));

        assertEquals("To register you have to be 18 years old", actual.getMessage());
    }

    @Test
    void searchUsersByBirthday() throws UserServiceException {
        UserEntity first = repository.save(new UserEntity(null,
                "email1@email.com",
                "first1",
                "last1",
                LocalDate.of(2000, 1, 1),
                "address1",
                "11111111111"));
        UserEntity second = repository.save(new UserEntity(null,
                "email2@email.com",
                "first2",
                "last2",
                LocalDate.of(2002, 2, 2),
                "address2",
                "22222222222"));
        UserEntity third = repository.save(new UserEntity(null,
                "email3@email.com",
                "first3",
                "last3",
                LocalDate.of(2003, 3, 3),
                "address3",
                "333333333"));
        UserEntity fourth = repository.save(new UserEntity(null,
                "email4@email.com",
                "first4",
                "last4",
                LocalDate.of(2004, 4, 4),
                "address4",
                "4444444444"));
        UserEntity fifth = repository.save(new UserEntity(null,
                "email5@email.com",
                "first5",
                "last5",
                LocalDate.of(2005, 5, 5),
                "address5",
                "555555555"));


        List<UserDto> actual = underTest.searchUsersByBirthday(LocalDate.of(2002, 1, 1),
                LocalDate.of(2004, 4, 4));

        List<UserDto> expected = List.of(new UserDto(second.getId(),
                        "email2@email.com",
                        "first2",
                        "last2",
                        LocalDate.of(2002, 2, 2),
                        "address2",
                        "22222222222"),
                (new UserDto(third.getId(),
                        "email3@email.com",
                        "first3",
                        "last3",
                        LocalDate.of(2003, 3, 3),
                        "address3",
                        "333333333")),
                (new UserDto(fourth.getId(),
                        "email4@email.com",
                        "first4",
                        "last4",
                        LocalDate.of(2004, 4, 4),
                        "address4",
                        "4444444444"))
        );
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void searchUsersByBirthday_whenFromExceedsTo_thenError() {
        UserServiceException exception = assertThrows(UserServiceException.class, () -> underTest.searchUsersByBirthday(LocalDate.of(2023, 1, 2),
                LocalDate.of(2020, 1, 1)));
        assertEquals("From should not exceed to ", exception.getMessage());
    }


}
