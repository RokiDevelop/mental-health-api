package com.kiryukhin.mental_health.servicesLogic;

import com.kiryukhin.mental_health.dtos.UserCreateDto;
import com.kiryukhin.mental_health.dtos.UserDto;
import com.kiryukhin.mental_health.exeptions.RegistrationFailedException;
import com.kiryukhin.mental_health.exeptions.UserIsBlockedException;
import com.kiryukhin.mental_health.mappers.UserMapper;
import com.kiryukhin.mental_health.models.User;
import com.kiryukhin.mental_health.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(UserCreateDto dto) {
        User userEntity = mapper.userCreateDtoToEntity(dto);
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

        userEntity.setIsSuperuser(false);
        userEntity.setBlocked(false);
        userEntity.setVerified(false);

        return repository.save(userEntity);
    }

    @Override
    public User createSuperuser(UserCreateDto dto) {
        User userEntity = mapper.userCreateDtoToEntity(dto);
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        userEntity.setIsSuperuser(true);
        userEntity.setBlocked(false);
        userEntity.setVerified(true);

        return repository.save(userEntity);
    }

    @Override
    public List<User> getUserList() {
        return repository.findAll();
    }

    @Override
    public User getById(Long id) {
        return repository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public User getByUsernameOrEmail(String usernameOrEmail) {
        Optional<User> userOptional = repository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        return userOptional.orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public User getByUsernameOrEmailAndIsBlockedFalse(String usernameOrEmail) {
        Optional<User> userOptional = repository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        User user = userOptional.orElseThrow(EntityNotFoundException::new);

        if (user.isBlocked()) {
            throw new UserIsBlockedException("user is blocked!");
        }
        return user;
    }

    @Override
    public User getByUsername(String username) {
        Optional<User> userOptional = repository.findByUsername(username);
        User user = userOptional.orElseThrow(EntityNotFoundException::new);
        return user;
    }

    @Override
    public User getByUsernameAndIsBlockedFalse(String username) {
        Optional<User> userOptional = repository.findByUsername(username);
        User user = userOptional.orElseThrow(EntityNotFoundException::new);

        if (user.isBlocked()) {
            throw new UserIsBlockedException("user is blocked!");
        }
        return user;
    }

    @Override
    public User getByEmail(String email) {
        Optional<User> userOptional = repository.findByEmail(email);
        User user = userOptional.orElseThrow(EntityNotFoundException::new);
        return user;
    }

    @Override
    public User getByEmailAndIsBlockedFalse(String email) throws UserIsBlockedException {
        Optional<User> userOptional = repository.findByEmail(email);
        User user = userOptional.orElseThrow(EntityNotFoundException::new);
        if (user.isBlocked()) {
            throw new UserIsBlockedException("user is blocked");
        }
        return user;
    }

    @Override
    public User updateUserByUsername(String username, UserDto dto) {
        var user = getByUsernameAndIsBlockedFalse(username);
        mapper.updatePartialFromUserDto(user, dto);
        return repository.save(user);
    }

    @Override
    public User updateUserByEmail(String email, UserDto dto) {
        var user = getByEmailAndIsBlockedFalse(email);
        mapper.updatePartialFromUserDto(user, dto);
        return repository.save(user);
    }

    private String generateRandomHash() {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[16];
        random.nextBytes(randomBytes);
        String randomString = Base64.getEncoder().encodeToString(randomBytes);

        return passwordEncoder.encode(randomString);
    }
}