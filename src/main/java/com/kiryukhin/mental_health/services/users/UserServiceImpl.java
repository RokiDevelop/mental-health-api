package com.kiryukhin.mental_health.services.users;

import com.kiryukhin.mental_health.dtos.UserCreateDto;
import com.kiryukhin.mental_health.dtos.UserUpdateDto;
import com.kiryukhin.mental_health.dtos.responses.UserResponseDto;
import com.kiryukhin.mental_health.exeptions.RegistrationFailedException;
import com.kiryukhin.mental_health.exeptions.UserIsBlockedException;
import com.kiryukhin.mental_health.mappers.UserMapper;
import com.kiryukhin.mental_health.models.User;
import com.kiryukhin.mental_health.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto createUser(UserCreateDto dto) {
        User userEntity = userMapper.userCreateDtoToEntity(dto);
        if (repository.existsByUsername(userEntity.getUsername())
                || repository.existsByEmail(userEntity.getEmail())) {
            throw new RegistrationFailedException(
                    String.format("User with username '%s' or email '%s' is already exists!",
                            userEntity.getUsername(), userEntity.getEmail()));
        }
        if (userEntity.getPassword() != null) {
            userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else {
            userEntity.setPassword(generateRandomHash());
        }

        userEntity.setSuperuser(false);
        userEntity.setBlocked(false);
        userEntity.setVerified(false);

        return userMapper.toUserResponseDto(
                repository.save(userEntity));
    }

    @Override
    public UserResponseDto createSuperuser(UserCreateDto dto) {
        User userEntity = userMapper.userCreateDtoToEntity(dto);
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        userEntity.setSuperuser(true);
        userEntity.setBlocked(false);
        userEntity.setVerified(true);

        return userMapper.toUserResponseDto(
                repository.save(userEntity));
    }

    @Override
    public List<UserResponseDto> getUserList() {
        List<User> users = repository.findAll();
        return users.stream().map(
                userMapper::toUserResponseDto).toList();
    }

    @Override
    public UserResponseDto getById(Long id) {
        User user = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto getByUsernameOrEmail(String usernameOrEmail) {
        Optional<User> userOptional = repository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto getByUsernameOrEmailAndIsBlockedFalse(String usernameOrEmail) {
        Optional<User> userOptional = repository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));

        if (user.isBlocked()) {
            throw new UserIsBlockedException("user is blocked!");
        }
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto getByUsername(String username) {
        log.info(this.getClass().getName() + " getByUsername.");
        Optional<User> userOptional = repository.findByUsername(username);
        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found with username: " + username));
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto getByUsernameAndIsBlockedFalse(String username) {
        Optional<User> userOptional = repository.findByUsername(username);
        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found with username: " + username));

        if (user.isBlocked()) {
            throw new UserIsBlockedException("user is blocked!");
        }
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto getByEmail(String email) {
        Optional<User> userOptional = repository.findByEmail(email);
        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found with email: " + email));
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto getByEmailAndIsBlockedFalse(String email) throws UserIsBlockedException {
        Optional<User> userOptional = repository.findByEmail(email);
        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found with email: " + email));
        if (user.isBlocked()) {
            throw new UserIsBlockedException("user is blocked");
        }
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto updateUserByUsername(String username, UserUpdateDto userUpdateDto) {
        Optional<User> userOptional = repository.findByUsername(username);
        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found with username: " + username));

        if (user.isBlocked()) {
            throw new UserIsBlockedException("user is blocked!");
        }
        userMapper.updatePartialFromUserUpdateDto(user, userUpdateDto);

        return userMapper.toUserResponseDto(
                repository.save(user));
    }

    @Override
    public UserResponseDto updateUserByEmail(String email, UserUpdateDto dto) {
        Optional<User> userOptional = repository.findByEmail(email);
        User user = userOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found with email: " + email));

        if (user.isBlocked()) {
            throw new UserIsBlockedException("user is blocked!");
        }
        userMapper.updatePartialFromUserUpdateDto(user, dto);
        return userMapper.toUserResponseDto(
                repository.save(user));
    }

    @Override
    public User getUserForUserDetails(String usernameOrEmail) {
        Optional<User> userOptional = repository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        return userOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));
    }

    @Override
    public Set<String> findUsernameSetByUsername(String username) {
        String pattern = username + "%";
        return repository.findUsernameSetByUsernameLike(pattern);
    }

    private String generateRandomHash() {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[16];
        random.nextBytes(randomBytes);
        String randomString = Base64.getEncoder().encodeToString(randomBytes);

        return passwordEncoder.encode(randomString);
    }
}