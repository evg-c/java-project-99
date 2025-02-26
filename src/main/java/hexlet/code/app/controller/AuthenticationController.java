package hexlet.code.app.controller;

import hexlet.code.app.dto.AuthRequest;
import hexlet.code.app.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для аутентификации пользователей в RESTful API.
 */
@RestController
@RequestMapping("/api")
public class AuthenticationController {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Метод выполняет аутентификацию. Аутентификация это POST запрос по пути /api/login.
     * @param authRequest - объект AuthRequest, который содержит поля email и password.
     * @return - возвращается JWT-токен в виде строки
     */
    @PostMapping("/login")
    public String create(@RequestBody AuthRequest authRequest) {
        var authentication = new UsernamePasswordAuthenticationToken(
                authRequest.getEmail(), authRequest.getPassword());

        authenticationManager.authenticate(authentication);

        var token = jwtUtils.generateToken(authRequest.getEmail());
        return token;
    }
}
