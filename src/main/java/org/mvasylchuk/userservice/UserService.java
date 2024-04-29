package org.mvasylchuk.userservice;

import org.mvasylchuk.userservice.dto.CreateUserRequest;
import org.mvasylchuk.userservice.dto.UpdateUserRequest;
import org.mvasylchuk.userservice.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {
    private final int minAge;
    private final UserRepository userRepository;

    public UserService(@Value("${userService.minAge}") int minAge, UserRepository userRepository) {
        this.minAge = minAge;
        this.userRepository = userRepository;
    }

    public UserDto createUser(CreateUserRequest request) throws UserServiceException {
        if (request.getBirthDate().plusYears(minAge).isAfter(LocalDate.now())) {
            throw new UserServiceException("To register you have to be 18 years old");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserServiceException("Users with provided email is exist");
        }
        UserEntity userEntity = new UserEntity(null,
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getBirthDate(),
                request.getAddress(),
                request.getPhoneNumber());

        userRepository.save(userEntity);

        return new UserDto(userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getBirthDate(),
                userEntity.getAddress(),
                userEntity.getPhoneNumber());

    }

    public UserDto updateAllUserFields(Long id, CreateUserRequest request) throws UserServiceException {
        if (request.getBirthDate().plusYears(minAge).isAfter(LocalDate.now())) {
            throw new UserServiceException("To register you have to be 18 years old");
        }
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new UserServiceException("User is not found"));

        userEntity.setFirstName(request.getFirstName());
        userEntity.setLastName(request.getLastName());
        userEntity.setEmail(request.getEmail());
        userEntity.setBirthDate(request.getBirthDate());
        userEntity.setAddress(request.getAddress());
        userEntity.setPhoneNumber(request.getPhoneNumber());

        userRepository.save(userEntity);

        return new UserDto(userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getBirthDate(),
                userEntity.getAddress(),
                userEntity.getPhoneNumber());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserDto updateSomeUserFields(Long id, UpdateUserRequest request) throws UserServiceException {

        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new UserServiceException("User is not found"));

        if (request.getEmail() != null) {
            userEntity.setEmail(request.getEmail());
        }
        if (request.getFirstName() != null) {
            userEntity.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            userEntity.setLastName(request.getLastName());
        }
        if (request.getBirthDate() != null) {
            if (request.getBirthDate().plusYears(minAge).isAfter(LocalDate.now())) {
                throw new UserServiceException("To register you have to be 18 years old");
            }
            userEntity.setBirthDate(request.getBirthDate());
        }
        request.getAddress().ifPresent((address) -> userEntity.setAddress(address));
        request.getPhoneNumber().ifPresent((phone) -> userEntity.setPhoneNumber(phone));

        userRepository.save(userEntity);

        return new UserDto(userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getBirthDate(),
                userEntity.getAddress(),
                userEntity.getPhoneNumber());

    }

    public List<UserDto> searchUsersByBirthday(LocalDate from, LocalDate to) throws UserServiceException {
        if (from.isAfter(to)) {
            throw new UserServiceException("From should not exceed to ");
        }

        List<UserEntity> entitiesList = userRepository.findAllByBirthDateIsBetween(from, to);

        return entitiesList.stream()
                .map(entity -> new UserDto(entity.getId(),
                        entity.getEmail(),
                        entity.getFirstName(),
                        entity.getLastName(),
                        entity.getBirthDate(),
                        entity.getAddress(),
                        entity.getPhoneNumber()))
                .toList();
    }

}
