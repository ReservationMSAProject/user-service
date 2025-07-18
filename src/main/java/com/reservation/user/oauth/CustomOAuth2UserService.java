package com.reservation.user.oauth;

import com.reservation.user.users.domain.UserEntity;
import com.reservation.user.users.enums.Roles;
import com.reservation.user.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;


    /**
     * OAuth2UserService를 구현하여 OAuth2 인증 후 사용자 정보를 처리합니다.
     * @param userRequest OAuth2UserRequest 객체
     * @return OAuth2User 객체
     * @throws OAuth2AuthenticationException 인증 예외 발생 시
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        UserEntity user = saveOrUpdate(attributes);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }

    private UserEntity saveOrUpdate(OAuthAttributes attributes) {
        return userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.updateOAuth2Info(attributes.getName(), attributes.getPicture()))
                .orElseGet(() -> {
                    UserEntity newUser = UserEntity.builder()
                            .name(attributes.getName())
                            .email(attributes.getEmail())
                            .picture(attributes.getPicture())
                            .role(Roles.USER)
                            .provider(attributes.getProvider())
                            .providerId(attributes.getProviderId())
                            .isActive(true)
                            .build();
                    return userRepository.save(newUser);
                });
    }
}
