package co.kr.social_login_test.infrastructure.config.security;

import co.kr.social_login_test.infrastructure.config.security.service.CustomLoginSuccess;
import co.kr.social_login_test.infrastructure.config.security.service.CustomOAuth2UserService;
import co.kr.social_login_test.infrastructure.filter.jwt.JWTFilter;
import co.kr.social_login_test.infrastructure.util.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomOAuth2UserService customOAuth2UserService;
  private final CustomLoginSuccess loginSuccess;
  private final JWTUtil jwtUtil;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .csrf(auth -> auth.disable())
            .formLogin(auth -> auth.disable())
            .httpBasic(auth -> auth.disable())
            .addFilterBefore(
                    new JWTFilter(jwtUtil),
                    UsernamePasswordAuthenticationFilter.class
            )
            .oauth2Login(oauth2 ->
                    oauth2
                            .userInfoEndpoint(
                                    (userInfoEndpointConfig ->
                                            userInfoEndpointConfig.userService(customOAuth2UserService))
                            )
                            .successHandler(loginSuccess)
            );

    // 경로 인가 작업
    http.authorizeHttpRequests(auth ->
            auth.requestMatchers("/").permitAll().anyRequest().authenticated()
    );

    // session setting
    http.sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    );
    return http.build();
  }
}
