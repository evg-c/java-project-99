package hexlet.code.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class JWTUtils {

    @Autowired
    private JwtEncoder encoder;

    /**
     * Метод, предназначенный для генерации JWT (JSON Web Token) на основе переданного email пользователя.
     * @param email - email пользователя.
     * Метод создает JWT токен с указанными параметрами: издателем ("issuer"), временем выдачи ("issuedAt"),
     *              временем истечения срока действия ("expiresAt"), и субъектом ("subject").
     * @return - возвращает сгенерированный токен
     */
    public String generateToken(String email) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(email)
                .build();
        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
