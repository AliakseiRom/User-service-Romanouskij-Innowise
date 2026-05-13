package user_service.user_service.UserServiceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import user_service.user_service.dto.UserRequestDto;
import user_service.user_service.dto.UserResponseDto;
import user_service.user_service.mapper.UserMapper;
import user_service.user_service.model.User;
import user_service.user_service.repository.UserRepository;
import user_service.user_service.service.UserService;

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

        when(userMapper.toEntity(requestDto))
                .thenReturn(user);

        when(userRepository.save(user))
                .thenReturn(savedUser);

        when(userMapper.toDto(savedUser))
                .thenReturn(responseDto);

        UserResponseDto result =
                userService.createUser(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(userRepository).save(user);
    }

    @Test
    void shouldGetUserById() {

        User user = new User();
        user.setId(1L);

        UserResponseDto responseDto =
                new UserResponseDto();

        responseDto.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userMapper.toDto(user))
                .thenReturn(responseDto);

        UserResponseDto result =
                userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldGetAllUsers() {

        Pageable pageable = PageRequest.of(0, 5);

        User user = new User();
        user.setId(1L);

        Page<User> page =
                new PageImpl<>(List.of(user));

        UserResponseDto responseDto =
                new UserResponseDto();

        responseDto.setId(1L);

        when(userRepository.findAll(
                any(Specification.class),
                eq(pageable)
        )).thenReturn(page);

        when(userMapper.toDto(user))
                .thenReturn(responseDto);

        Page<UserResponseDto> result =
                userService.getAllUsers(
                        "Alex",
                        "Ivanov",
                        pageable
                );

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldUpdateUser() {

        Long userId = 1L;

        UserRequestDto requestDto =
                new UserRequestDto();

        requestDto.setName("Updated");

        User existingUser = new User();
        existingUser.setId(userId);

        User mappedUser = new User();
        mappedUser.setName("Updated");

        UserResponseDto responseDto =
                new UserResponseDto();

        responseDto.setName("Updated");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        when(userMapper.toEntity(requestDto))
                .thenReturn(mappedUser);

        when(userRepository.save(existingUser))
                .thenReturn(existingUser);

        when(userMapper.toDto(existingUser))
                .thenReturn(responseDto);

        UserResponseDto result =
                userService.updateUser(userId, requestDto);

        assertNotNull(result);
        assertEquals("Updated", result.getName());

        verify(userRepository).save(existingUser);
    }

    @Test
    void shouldActivateUser() {

        User user = new User();
        user.setId(1L);
        user.setActive(false);

        UserResponseDto responseDto =
                new UserResponseDto();

        responseDto.setActive(true);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userMapper.toDto(user))
                .thenReturn(responseDto);

        UserResponseDto result =
                userService.activateUser(1L);

        assertTrue(user.isActive());
        assertTrue(result.isActive());

        verify(userRepository).save(user);
    }

    @Test
    void shouldDeactivateUser() {

        User user = new User();
        user.setId(1L);
        user.setActive(true);

        UserResponseDto responseDto =
                new UserResponseDto();

        responseDto.setActive(false);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userMapper.toDto(user))
                .thenReturn(responseDto);

        UserResponseDto result =
                userService.deactivateUser(1L);

        assertFalse(user.isActive());
        assertFalse(result.isActive());

        verify(userRepository).save(user);
    }

    @Test
    void shouldDeleteUser() {

        Long userId = 1L;

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }
}