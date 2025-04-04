package co.kr.social_login_test.infrastructure.util.jwt;

import co.kr.jwt.util.AbstractJwtUtils;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
public class JWTUtil extends AbstractJwtUtils {
  // AbstractJwtUtils 를 사용하기 위해서는 application.yml 또는 application.properties 에서 값을 읽어 와야 함
  public JWTUtil(@Value("${jwt}") String secretKey) {
    super(new SecretKeySpec(
            secretKey.getBytes(StandardCharsets.UTF_8),
            Jwts.SIG.HS256.key().build().getAlgorithm()
    ));
  }

  // 아래에 추가적인 메서드를 구현할 수 있습니다.
  public String getPhone(String token) {
    // parseClaims 메서드를 통해서 토큰 내부의 다른 값에 접근할 수 있습니다.
    return parseClaims(token).get("phone").toString();
  }
}
