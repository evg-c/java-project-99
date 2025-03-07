package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.service.TaskStatusService;
import jakarta.validation.Valid;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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

    /**
     * Обработчик GET-запроса по маршруту /tasks.
     * @return - возвращает список пользователей в формате ResponseEntity.
     */
    @GetMapping(path = "/tasks")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TaskDTO>> indexTask() {
        var tasks = taskRepository.findAll();
        var result = tasks.stream()
                .map(task -> taskMapper.map(task))
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(result.size()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    /**
     * Обработчик GET-запроса по маршруту /tasks/{id}.
     * @param id - идентификатор задачи
     * @return - возвращает конкретную задачу в формате ResponseEntity.
     */
    @GetMapping(path = "/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDTO> showTask(@PathVariable Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        var dto = taskMapper.map(task);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto);
    }

    /**
     * Обработчик POST-запроса по маршруту /tasks.
     * @param data - запрос на создание задачи в формате TaskCreateDTO
     * @return - возвращает созданного пользователя в формате ResponseEntity.
     */
    @PostMapping(path = "/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDTO> createTask(@RequestBody @Valid TaskCreateDTO data) {
        var task = taskMapper.map(data);
        task = taskStatusService.defineTaskStatus(task);
        taskRepository.save(task);
        var dto = taskMapper.map(task);
        return ResponseEntity.created(URI.create("/tasks"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto);
    }

    /**
     * Обработчик PUT-запроса по маршруту /tasks/{id}.
     * @param id - идентификатор задачи
     * @param data - запрос на изменение задачи в формате TaskUpdateDTO
     * @return - возвращает измененную задачу в формате ResponseEntity.
     */
    @PutMapping(path = "/tasks/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskDTO> updateTask(@Valid @RequestBody TaskUpdateDTO data,
                                              @PathVariable Long id) {
        var task =taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        taskMapper.update(data, task);
        var taskWithStatus = taskStatusService.defineTaskStatus(task);
        taskRepository.save(taskWithStatus);
        var dto = taskMapper.map(task);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto);
    }

    /**
     * Обработчик DELETE-запроса по маршруту /tasks/{id}.
     * @param id - идентификатор задачи
     */
    @DeleteMapping(path = "/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
    }
}
