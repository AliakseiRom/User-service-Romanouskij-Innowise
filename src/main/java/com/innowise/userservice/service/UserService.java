package com.innowise.userservice.service;

import com.innowise.userservice.model.User;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.specifications.UserSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.innowise.userservice.dto.UserRequestDto;
import com.innowise.userservice.dto.UserResponseDto;
import com.innowise.userservice.mapper.UserMapper;



import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = userMapper.toEntity(userRequestDto);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public UserResponseDto getUserById(Long id) {
        return userMapper.toDto(userRepository.findById(id).orElse(null));
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
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        Optional<User> userOptional = userRepository.findById(id);
        User user = userMapper.toEntity(userRequestDto);

        if (userOptional.isPresent()) {
            User userToUpdate = userOptional.get();
            userToUpdate.setName(user.getName());
            userToUpdate.setSurname(user.getSurname());
            userToUpdate.setBirthDate(user.getBirthDate());
            userToUpdate.setEmail(user.getEmail());
            return userMapper.toDto(userRepository.save(userToUpdate));
        }

        return null;
    }

    @Transactional
    public UserResponseDto activateUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        User user = userOptional.orElse(null);
        if (user != null) {
            user.setActive(true);
            userRepository.save(user);
            return userMapper.toDto(user);
        }

        return null;
    }

    @Transactional
    public UserResponseDto deactivateUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        User user = userOptional.orElse(null);
        if (user != null) {
            user.setActive(false);
            userRepository.save(user);
            return userMapper.toDto(user);
        }
        return null;
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
