package hexlet.code.app.service;

import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    /**
     * Метод создания статусов задач при начальной инициализации.
     * @param name - имя статуса, передаваемый из DataInitializer.
     * @param slug - слаг, передаваемый из DataInitializer.
     */
    public void createTaskStatus(String name, String slug) {

        var taskStatus = new TaskStatus();
        taskStatus.setName(name);
        taskStatus.setSlug(slug);
        taskStatusRepository.save(taskStatus);
    }

    public TaskStatus defineTaskStatus(Task model) {
        var oldTaskStatus = model.getTaskStatus();
        var slug = oldTaskStatus.getSlug();
        var newTaskStatus = taskStatusRepository.findBySlug(slug).get();
        //model.setTaskStatus(newTaskStatus);
        return newTaskStatus;
    }
}
