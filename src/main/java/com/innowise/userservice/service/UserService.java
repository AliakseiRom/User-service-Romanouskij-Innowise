package com.innowise.userservice.service;

import com.innowise.userservice.dto.CreateUserRequest;
import com.innowise.userservice.dto.CreateUserResponse;
import com.innowise.userservice.dto.UserRequestDto;
import com.innowise.userservice.dto.UserResponseDto;
import com.innowise.userservice.exceptions.*;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.User;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.security.JwtUser;
import com.innowise.userservice.security.SecurityUtils;
import com.innowise.userservice.specifications.UserSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = userMapper.toEntity(userRequestDto);

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserWithEmailAlreadyExists("User with email " +
                    user.getEmail() +
                    " already exists"
            );
        }

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Cacheable(value = "users", key = "#id")
    public UserResponseDto getUserById(Long id) {
        JwtUser currentUser = SecurityUtils.getCurrentUser();

        boolean isAdmin = currentUser.getRole().equals("ADMIN");
        boolean isOwner = currentUser.getUserId().equals(id);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Access denied");
        }

        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }

        return userMapper.toDto(user.get());
    }

    public Page<UserResponseDto> getAllUsers(
            String name,
            String surname,
            Pageable pageable
    ) {
        Specification<User> spec = Specification.allOf();

        if (name != null && !name.isBlank()) {
            spec = spec.and(UserSpecification.hasName(name));
        }

        if (surname != null && !surname.isBlank()) {
            spec = spec.and(UserSpecification.hasSurname(surname));
        }

        return userRepository.findAll(spec, pageable).map(userMapper::toDto);
    }

    @Transactional
    @CachePut(value = "users", key = "#id")
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        JwtUser currentUser = SecurityUtils.getCurrentUser();

        boolean isAdmin = currentUser.getRole().equals("ADMIN");
        boolean isOwner = currentUser.getUserId().equals(id);

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Access denied");
        }

        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User with id " + id + " not found"
                        )
                );

        Optional<User> existingUser =
                userRepository.findByEmail(userRequestDto.getEmail());

        if (existingUser.isPresent()
                && !existingUser.get().getId().equals(id)) {

            throw new UserWithEmailAlreadyExists(
                    "User with email " +
                            userRequestDto.getEmail() +
                            " already exists"
            );
        }

        userToUpdate.setName(userRequestDto.getName());
        userToUpdate.setSurname(userRequestDto.getSurname());
        userToUpdate.setBirthDate(userRequestDto.getBirthDate());
        userToUpdate.setEmail(userRequestDto.getEmail());

        return userMapper.toDto(userRepository.save(userToUpdate));
    }

    @Transactional
    public UserResponseDto activateUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }
        User user = userOptional.get();
        user.setActive(true);
        userRepository.save(user);
        return userMapper.toDto(user);

    }

    @Transactional
    public UserResponseDto deactivateUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }
        User user = userOptional.get();
        user.setActive(false);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public CreateUserResponse createInternalUser(CreateUserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserWithEmailAlreadyExists(
                    "User with email " + request.getEmail() + " already exists"
            );
        }

        User user = new User();

        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setBirthDate(request.getBirthDate());
        user.setEmail(request.getEmail());
        user.setActive(true);

        User savedUser = userRepository.save(user);

        return new CreateUserResponse(savedUser.getId());
    }

    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));

        return userMapper.toDto(user);
    }

    @Transactional
    public void rollbackUserCreation(String email) {
        userRepository.findByEmail(email)
                .ifPresent(userRepository::delete);
    }
}
