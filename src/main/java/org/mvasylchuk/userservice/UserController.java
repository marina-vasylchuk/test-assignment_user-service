package org.mvasylchuk.userservice;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mvasylchuk.userservice.dto.CreateUserRequest;
import org.mvasylchuk.userservice.dto.UpdateUserRequest;
import org.mvasylchuk.userservice.dto.UserDto;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping()
    public BaseResponse<UserDto> create(@RequestBody @Valid CreateUserRequest request) throws UserServiceException {
        return new BaseResponse<>(userService.createUser(request), null);
    }

    @PutMapping("/{id}")
    public BaseResponse<UserDto> updateComplete(@PathVariable Long id, @RequestBody @Valid CreateUserRequest request) throws UserServiceException {
        return new BaseResponse<>(userService.updateAllUserFields(id, request), null);
    }

    @PatchMapping("/{id}")
    public BaseResponse<UserDto> updatePartial(@PathVariable Long id, @RequestBody @Valid UpdateUserRequest request) throws UserServiceException {
        return new BaseResponse<>(userService.updateSomeUserFields(id, request), null);
    }

    @GetMapping()
    public BaseResponse<List<UserDto>> searchByBirthday(@RequestParam(name = "from") LocalDate from,
                                                        @RequestParam(name = "to") LocalDate to) throws UserServiceException {
        return new BaseResponse<>(userService.searchUsersByBirthday(from, to), null);
    }


    @DeleteMapping("/{id}")
    public BaseResponse<String> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return new BaseResponse<>("user " + id + " is deleted", null);
    }

}
