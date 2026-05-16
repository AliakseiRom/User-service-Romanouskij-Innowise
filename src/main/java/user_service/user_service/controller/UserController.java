package user_service.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import user_service.user_service.dto.UserRequestDto;
import user_service.user_service.dto.UserResponseDto;
import user_service.user_service.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto) {
        UserResponseDto userResponseDto = userService.createUser(userRequestDto);
        return new ResponseEntity<>(userResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto userResponseDto = userService.getUserById(id);
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @RequestParam String name,

            @RequestParam String surname,

            Pageable pageable
    ) {
        return new ResponseEntity<>(userService.getAllUsers(name, surname, pageable), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,

            @RequestBody UserRequestDto userRequestDto
    ) {
        UserResponseDto userResponseDto = userService.updateUser(id, userRequestDto);
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<UserResponseDto> activateUser(@PathVariable Long id) {
        UserResponseDto userResponseDto = userService.activateUser(id);
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<UserResponseDto> deactivateUser(@PathVariable Long id) {
        UserResponseDto userResponseDto = userService.deactivateUser(id);
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }
}
