package co.kr.social_login_test.infrastructure.config.security.service;

import co.kr.oauth2.dto.OAuth2Response;
import co.kr.oauth2.factory.OAuth2ResponseFactory;
import co.kr.oauth2.service.LibOAuth2UserService;
import co.kr.social_login_test.infrastructure.config.security.dto.CustomOAuth2User;
import co.kr.social_login_test.infrastructure.config.security.dto.UserDTO;
import co.kr.social_login_test.member.model.Member;
import co.kr.social_login_test.member.service.MemberService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@ComponentScan(basePackages = "co.kr.oauth2")
public class CustomOAuth2UserService extends LibOAuth2UserService {
  private final MemberService memberService;
  protected CustomOAuth2UserService(OAuth2ResponseFactory auth2ResponseFactory, MemberService memberService) {
    super(auth2ResponseFactory);
    this.memberService = memberService;
  }

  @Override
  protected OAuth2User createCustomOAuth2User(OAuth2Response response, Map<String, Object> attribute) {
    String username = response.getProvider() + " " + response.getProviderId();

    Member member = memberService.findMember(username);

    return checkAlreadyMember(username, member, response);
  }

  private CustomOAuth2User checkAlreadyMember(String username, Member data, OAuth2Response response) {
    if (data == null) {
      Member member = Member.builder()
              .username(username)
              .email(response.getEmail())
              .name(response.getName())
              .role("ROLE_USER")
              .build();

      memberService.save(member);

      UserDTO userDTO = UserDTO.builder()
              .username(username)
              .name(response.getName())
              .role("ROLE_USER")
              .build();

      return new CustomOAuth2User(userDTO);
    } else {
      data.setEmail(response.getEmail());
      data.setName(response.getName());

      UserDTO userDTO = new UserDTO();
      userDTO.setUsername(data.getUsername());
      userDTO.setName(response.getName());
      userDTO.setRole(data.getRole());

      return new CustomOAuth2User(userDTO);
    }
  }
}
