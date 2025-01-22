package com.kiryukhin.mental_health.security.oauth;

import com.kiryukhin.mental_health.dtos.RoleDto;
import com.kiryukhin.mental_health.dtos.UserCreateDto;
import com.kiryukhin.mental_health.dtos.UserDto;
import com.kiryukhin.mental_health.dtos.responses.UserResponseDto;
import com.kiryukhin.mental_health.exeptions.UserIsBlockedException;
import com.kiryukhin.mental_health.mappers.UserMapper;
import com.kiryukhin.mental_health.models.User;
import com.kiryukhin.mental_health.repositories.UserRepository;
import com.kiryukhin.mental_health.security.CustomOauthUserDetails;
import com.kiryukhin.mental_health.services.users.RoleService;
import com.kiryukhin.mental_health.services.users.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;
    private final RoleService roleService;
    private final List<OAuth2UserInfoExtractor> oAuth2UserInfoExtractors;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws UserIsBlockedException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Optional<OAuth2UserInfoExtractor> oAuth2UserInfoExtractorOptional =
                oAuth2UserInfoExtractors.stream()
                        .filter(oAuth2UserInfoExtractor -> oAuth2UserInfoExtractor.accepts(userRequest))
                        .findFirst();
        if (oAuth2UserInfoExtractorOptional.isEmpty()) {
            throw new InternalAuthenticationServiceException("The OAuth2 provider is not supported yet");
        }
        CustomOauthUserDetails customUserDetails;
        if (userRequest.getClientRegistration().getRegistrationId().equals("vk")) {
            customUserDetails = oAuth2UserInfoExtractorOptional.get().extractUserInfo(oAuth2User, userRequest);
        } else {
            customUserDetails = oAuth2UserInfoExtractorOptional.get().extractUserInfo(oAuth2User);
        }

        UserResponseDto user = upsertUser(customUserDetails);
        customUserDetails.setId(user.getId());

        return customUserDetails;
    }

    private UserResponseDto upsertUser(CustomOauthUserDetails customUserDetails) throws UserIsBlockedException {
        UserDto existingUserDto;
        try {
            User existingUser = userRepository.getByEmail(
                    customUserDetails.getEmail());
            if (existingUser == null) {
                throw new EntityNotFoundException();
            }

            if (existingUser.isBlocked()) {
                throw new UserIsBlockedException("User is blocked");
            }
            existingUserDto = userMapper.toUserDto(existingUser);
            boolean isUpdated = updateUserDto(existingUserDto, customUserDetails);
            if (isUpdated) {
                userMapper.updatePartialFromUserDto(existingUser, existingUserDto);
                userRepository.save(existingUser);
            }
            return userMapper.toUserResponseDto(existingUser);

        } catch (EntityNotFoundException e) {
            RoleDto role =
                    roleService.getRoles().stream().filter(x -> x.getName().equals("ROLE_USER")).findFirst().get();

            UserCreateDto user = new UserCreateDto();
            user.setFirstName(customUserDetails.getFirstName());
            user.setLastName(customUserDetails.getLastName());
            user.setUsername(customUserDetails.getUsername());
            user.setEmail(customUserDetails.getEmail());
            user.setAuthProvider(customUserDetails.getProvider());
            user.setRoles(Set.of(role));
            return userService.createUser(user);
        } catch (UserIsBlockedException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    private boolean updateUserDto(UserDto userDto, CustomOauthUserDetails customUserDetails) {
        boolean isUpdated = false;

        isUpdated |= updateFieldIfEmpty(userDto::getUsername, userDto::setUsername, customUserDetails.getUsername());
        isUpdated |= updateFieldIfEmpty(userDto::getFirstName, userDto::setFirstName, customUserDetails.getFirstName());
        isUpdated |= updateFieldIfEmpty(userDto::getLastName, userDto::setLastName, customUserDetails.getLastName());

        if (userDto.getAuthProvider() == null || !userDto.getAuthProvider().equals(customUserDetails.getProvider())) {
            userDto.setAuthProvider(customUserDetails.getProvider());
            isUpdated = true;
        }

        return isUpdated;
    }

    private <T> boolean updateFieldIfEmpty(Supplier<T> getter, Consumer<T> setter, T newValue) {
        T currentValue = getter.get();
        if (currentValue == null || (currentValue instanceof String && ((String) currentValue).isEmpty())) {
            setter.accept(newValue);
            return true;
        }
        return false;
    }
}
