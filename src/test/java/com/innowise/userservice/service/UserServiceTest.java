package com.innowise.userservice.service;

import com.innowise.userservice.dto.UserRequestDto;
import com.innowise.userservice.dto.UserResponseDto;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.User;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.security.JwtUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        mockSecurity(1L, "ADMIN");
    }

    private void mockSecurity(Long userId, String role) {
        JwtUser jwtUser = new JwtUser(userId, "test", role);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        jwtUser,
                        null,
                        List.of(() -> "ROLE_" + role)
                );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void shouldCreateUserSuccessfully() {

        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setName("Alex");
        requestDto.setSurname("Ivanov");
        requestDto.setBirthDate(LocalDate.of(2000, 1, 1));
        requestDto.setEmail("alex@test.com");
        requestDto.setActive(true);

        User user = new User();
        User savedUser = new User();
        savedUser.setId(1L);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(1L);

        when(userMapper.toEntity(requestDto)).thenReturn(user);
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(responseDto);

        UserResponseDto result = userService.createUser(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(userRepository).save(user);
    }

    @Test
    void shouldGetUserById() {

        User user = new User();
        user.setId(1L);

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.getUserById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void shouldGetAllUsers() {

        Pageable pageable = PageRequest.of(0, 5);

        User user = new User();
        user.setId(1L);

        Page<User> page = new PageImpl<>(List.of(user));

        UserResponseDto dto = new UserResponseDto();
        dto.setId(1L);

        when(userRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(userMapper.toDto(user)).thenReturn(dto);

        Page<UserResponseDto> result =
                userService.getAllUsers("Alex", "Ivanov", pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldUpdateUser() {

        Long id = 1L;

        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setEmail("new@test.com");

        User user = new User();
        user.setId(id);
        user.setEmail("old@test.com");

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setEmail("new@test.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.updateUser(id, requestDto);

        assertEquals("new@test.com", result.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void shouldActivateUser() {

        User user = new User();
        user.setId(1L);
        user.setActive(false);

        UserResponseDto dto = new UserResponseDto();
        dto.setActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserResponseDto result = userService.activateUser(1L);

        assertTrue(result.isActive());
    }

    @Test
    void shouldDeactivateUser() {

        User user = new User();
        user.setId(1L);
        user.setActive(true);

        UserResponseDto dto = new UserResponseDto();
        dto.setActive(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserResponseDto result = userService.deactivateUser(1L);

        assertFalse(result.isActive());
    }

    @Test
    void shouldDeleteUser() {

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }
}