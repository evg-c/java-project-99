package hexlet.code.app.component;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.CustomUserDetailService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
/**
 * Класс начальной инициализации данных.
 */
@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailService userService;

    /**
     * Метод стартует при запуске приложения (run).
     * @param args - аргументы запущенного приложения.
     * Создается начальный пользователь с email hexlet@example.com
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        var userData = new User();
        userData.setFirstName("hexlet");
        userData.setEmail("hexlet@example.com");
        userData.setPassword("qwerty");
        userService.createUser(userData);
    }
}
