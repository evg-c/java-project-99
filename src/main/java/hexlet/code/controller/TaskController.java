package hexlet.code.controller;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import hexlet.code.specification.TaskSpecification;
//import io.sentry.Sentry;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskStatusService taskStatusService;

    @Autowired
    private TaskSpecification taskSpecification;

    /**
     * Обработчик GET-запроса по маршруту /tasks.
     * @param params - параметры запроса в формате TaskParamsDTO&
     * @param page - номер выводимой страницы
     * @return - возвращает список пользователей в формате ResponseEntity.
     */
    @GetMapping(path = "/tasks")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TaskDTO>> index(TaskParamsDTO params, @RequestParam(defaultValue = "1") int page) {
        var specification = taskSpecification.build(params);
        var tasks = taskRepository.findAll(specification, PageRequest.of(page - 1, 10));
        var resultPage = tasks.map(taskMapper::map);
        var bodyResponse = resultPage.getContent();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(bodyResponse.size()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(bodyResponse);
    }

    /**
     * Обработчик GET-запроса по маршруту /tasks/{id}.
     * @param id - идентификатор задачи
     * @return - возвращает конкретную задачу в формате ResponseEntity.
     */
    @GetMapping(path = "/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO showTask(@PathVariable Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        var dto = taskMapper.map(task);
        return dto;
    }

    /**
     * Обработчик POST-запроса по маршруту /tasks.
     * @param data - запрос на создание задачи в формате TaskCreateDTO
     * @return - возвращает созданного пользователя в формате ResponseEntity.
     */
    @PostMapping(path = "/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO createTask(@RequestBody @Valid TaskCreateDTO data) {
        var task = taskMapper.map(data);
        taskRepository.save(task);
        TaskStatus taskStatus = task.getTaskStatus();
        if (taskStatus != null) {
            taskStatus.addTask(task);
        }
        User userTask = task.getAssignee();
        if (userTask != null) {
            userTask.addTask(task);
        }
        var dto = taskMapper.map(task);
        return dto;
    }

    /**
     * Обработчик PUT-запроса по маршруту /tasks/{id}.
     * @param id - идентификатор задачи
     * @param data - запрос на изменение задачи в формате TaskUpdateDTO
     * @return - возвращает измененную задачу в формате ResponseEntity.
     */
    @PutMapping(path = "/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO updateTask(@Valid @RequestBody TaskUpdateDTO data,
                                              @PathVariable Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        taskMapper.update(data, task);
        taskRepository.save(task);
        var dto = taskMapper.map(task);
        return dto;
    }

    /**
     * Обработчик DELETE-запроса по маршруту /tasks/{id}.
     * @param id - идентификатор задачи
     */
    @DeleteMapping(path = "/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        taskRepository.deleteById(id);
    }
}
