package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.repository.TaskStatusRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskStatusController {

    @Autowired
    private TaskStatusRepository repository;

    @Autowired
    private TaskStatusMapper mapper;

    /**
     * Обработчик GET-запроса по маршруту /task_statuses.
     * @return - возвращает список пользователей в формате ResponseEntity.
     */
    @GetMapping(path = "/task_statuses")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TaskStatusDTO>> indexTaskStatuses() {
        var taskStatuses = repository.findAll();
        var result = taskStatuses.stream()
                .map(mapper::map)
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(result.size()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    /**
     * Обработчик GET-запроса по маршруту /task_statuses/{id}.
     * @param id - идентификатор пользователя
     * @return - возвращает конкретного пользователя в формате ResponseEntity.
     */
    @GetMapping(path = "/task_statuses/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TaskStatusDTO> showTaskStatus(@PathVariable Long id) {
        var taskStatus = repository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id " + id + " not found"));
        var dto = mapper.map(taskStatus);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto);
    }

    /**
     * Обработчик POST-запроса по маршруту /task_statuses.
     * @param data - запрос на создание пользователя в формате UserCreateDTO
     * @return - возвращает созданного пользователя в формате ResponseEntity.
     */
    @PostMapping(path = "/task_statuses")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskStatusDTO> createTaskStatus(@RequestBody @Valid TaskStatusCreateDTO data) {
        var taskStatus = mapper.map(data);
        repository.save(taskStatus);
        var dto = mapper.map(taskStatus);
        return ResponseEntity.created(URI.create("/task_statuses"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto);
    }

    /**
     * Обработчик PUT-запроса по маршруту /task_statuses/{id}.
     * @param id - идентификатор пользователя
     * @param data - запрос на изменение пользователя в UserUpdateDTO
     * @return - возвращает измененного пользователя в формате ResponseEntity.
     */
    @PutMapping(path = "/task_statuses/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TaskStatusDTO> updateTaskStatus(@Valid @RequestBody TaskStatusUpdateDTO data,
                                                          @PathVariable Long id) {
        var taskStatus = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "TaskStatus with id " + id + " not found"));
        mapper.update(data, taskStatus);
        repository.save(taskStatus);
        var dto = mapper.map(taskStatus);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto);
    }

    /**
     * Обработчик DELETE-запроса по маршруту /task_statuses/{id}.
     * @param id - идентификатор пользователя
     */
    @DeleteMapping(path = "/task_statuses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void deleteTaskStatus(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
