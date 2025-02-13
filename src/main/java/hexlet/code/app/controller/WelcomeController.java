package hexlet.code.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    /**
     * Метод welcome обрабатывает GET-запросы к корневой странице приложения по пути /welcome.
     * Для этого нужно на корневой странице приложения указать путь /welcome.
     * @return Результатом работы будет строка приветствия
     */
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to Spring";
    }
}
