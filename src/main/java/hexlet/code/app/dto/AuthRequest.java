package hexlet.code.app.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Объект AuthRequest для создания запроса на аутентификацию.
 * Содержит поля email и password.
 */
@Getter
@Setter
public class AuthRequest {
    private String email;
    private String password;
}
