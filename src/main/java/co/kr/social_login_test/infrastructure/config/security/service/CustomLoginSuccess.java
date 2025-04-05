package co.kr.social_login_test.infrastructure.config.security.service;

import co.kr.social_login_test.infrastructure.config.security.dto.CustomOAuth2User;
import co.kr.social_login_test.infrastructure.util.jwt.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class CustomLoginSuccess extends SimpleUrlAuthenticationSuccessHandler {
  private final JWTUtil jwtUtil;

  @Override
  public void onAuthenticationSuccess(
          HttpServletRequest request,
          HttpServletResponse response,
          Authentication authentication
  ) throws IOException, ServletException {
    CustomOAuth2User oAuth2User =
            (CustomOAuth2User) authentication.getPrincipal();

    String username = oAuth2User.getUsername();
    Collection<? extends GrantedAuthority> authorities =
            authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();
    String role = auth.getAuthority();
    String token = jwtUtil.createJwt(username, role, 1000 * 60 * 60L, null);

    // 쿠키 저장 위치 이름 수정 가능
    response.addCookie(createCookie("Authorization", token));
    super.onAuthenticationSuccess(request, response, authentication);
  }

  private Cookie createCookie(String key, String value) {
    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(60 * 60 * 60);
    cookie.setPath("/");
    cookie.setHttpOnly(true);

    return cookie;
  }
}
