package co.kr.social_login_test.infrastructure.filter.jwt;

import co.kr.jwt.filter.AbstractJwtFilter;
import co.kr.jwt.util.JwtUtils;
import co.kr.social_login_test.infrastructure.config.security.dto.CustomOAuth2User;
import co.kr.social_login_test.infrastructure.config.security.dto.UserDTO;
import co.kr.social_login_test.infrastructure.util.jwt.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Log4j2
public class JWTFilter extends AbstractJwtFilter {

  public JWTFilter(JwtUtils jwtUtils) {
    super(jwtUtils);
  }

  // 기본적으로 토큰이 null 이거나 만료가 되어 있을 경우 401 응답과
  // "Unauthorized: Invalid or expired token" 메세지를 반환해 줍니다.
  // 이 메서드에서 추가적인 검증을 수행하거나 검증 이후의 명령을 수행하게 할 수 있습니다.
  @Override
  protected void validateJwt(HttpServletRequest request, HttpServletResponse response) {
    String token = getToken(request, response);

    // 아래 코드를 통해 jwtutil 에서 만든 getPhone 메서드를 사용할 수 있습니다.
    String phone = ((JWTUtil) jwtUtils).getPhone(token);

    //토큰에서 username과 role 획득
    String username = jwtUtils.getUsername(token);
    String role = jwtUtils.getRole(token);

    //userDTO를 생성하여 값 set
    UserDTO userDTO = new UserDTO();
    userDTO.setUsername(username);
    userDTO.setRole(role);

    //UserDetails에 회원 정보 객체 담기
    CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

    //스프링 시큐리티 인증 토큰 생성
    Authentication authToken = new UsernamePasswordAuthenticationToken(
            customOAuth2User,
            null,
            customOAuth2User.getAuthorities()
    );
    //세션에 사용자 등록
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }

  // 이 메서드는 필수적으로 구현을 해 주어야 합니다.
  // 자신이 저장한 토큰의 위치에 따라 헤더에서 가져오기 or 쿠키에서 가져오기
  // 저는 쿠키중 token 으로 저장된 이름에서 얻어오기로 했습니다.
  @Override
  protected String getToken(HttpServletRequest request, HttpServletResponse response) {
    Cookie[] cookies = request.getCookies();

    String token = null;
    if(cookies != null) {
      for (Cookie cookie : cookies) {
        log.info(cookie.getName());
        log.info(cookie.getValue());
        if (cookie.getName().equals("Authorization")) {
          token = cookie.getValue();
        }
      }
    }
    return token;
  }

}
