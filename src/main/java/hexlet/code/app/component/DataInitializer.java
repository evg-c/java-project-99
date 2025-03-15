package hexlet.code.app.component;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.CustomUserDetailService;
import hexlet.code.app.service.LabelService;
import hexlet.code.app.service.TaskStatusService;
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
    private CustomUserDetailService userService;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    /**
     * Метод начальной инициализации.
     * Стартует при запуске приложения (run).
     * @param args - аргументы запущенного приложения.
     * Создаются:
     *            начальный пользователь с email hexlet@example.com
     *            начальные статусы
     *            начальные метки
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (userRepository.findAll().isEmpty()) {
            var userData = new User();
            userData.setFirstName("hexlet");
            userData.setEmail("hexlet@example.com");
            userData.setPassword("qwerty");
            userService.createUser(userData);
        }

        if (taskStatusRepository.findAll().isEmpty()) {
            taskStatusService.createTaskStatus("Draft", "draft");
            taskStatusService.createTaskStatus("ToReview", "to_review");
            taskStatusService.createTaskStatus("ToBeFixed", "to_be_fixed");
            taskStatusService.createTaskStatus("ToPublish", "to_publish");
            taskStatusService.createTaskStatus("Published", "published");
        }

        if (labelRepository.findAll().isEmpty()) {
            labelService.createLabel("feature");
            labelService.createLabel("bug");
        }
    }
}
