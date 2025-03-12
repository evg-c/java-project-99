package hexlet.code.app.util;

import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ModelGenerator {

    private Model<User> userModel;

    private Model<TaskStatus> taskStatusModel;

    private Model<Task> taskModel;

    private Model<Label> labelModel;

    @Autowired
    private Faker faker;

    @PostConstruct
    private void init() {
        userModel = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPassword), () -> faker.internet().password(3, 100))
                .toModel();

        taskStatusModel = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .supply(Select.field(TaskStatus::getName), () -> faker.name().name())
                .supply(Select.field(TaskStatus::getSlug), () -> faker.internet().slug())
                .toModel();

        labelModel = Instancio.of(Label.class)
                .ignore(Select.field(Label::getId))
                .supply(Select.field(Label::getName), () -> faker.name().name())
                .toModel();

//        TaskStatus testTaskStatus;
//        User testuser;
//        testuser = Instancio.of(userModel).create();
//        testTaskStatus = Instancio.of(taskStatusModel).create();
//        taskModel = Instancio.of(Task.class)
//                .ignore(Select.field(Task::getId))
//                .supply(Select.field(Task::getIndex), () -> faker.number().randomNumber())
//                .supply(Select.field(Task::getName), () -> faker.name().name())
//                .supply(Select.field(Task::getDescription), () -> faker.gameOfThrones().quote())
//                .supply(Select.field(Task::getTaskStatus), () -> testTaskStatus)
//                .supply(Select.field(Task::getAssignee), () -> testuser)
//                .toModel();
    }
}
