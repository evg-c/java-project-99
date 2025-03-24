package hexlet.code.controller;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

//import java.net.URI;
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
     * @param id - идентификатор статуса
     * @return - возвращает конкретную задачу в формате ResponseEntity.
     */
    @GetMapping(path = "/task_statuses/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO showTaskStatus(@PathVariable Long id) {
        var taskStatus = repository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("TaskStatus with id " + id + " not found"));
        var dto = mapper.map(taskStatus);
        //return ResponseEntity.ok()
        //        .contentType(MediaType.APPLICATION_JSON)
        //        .body(dto);
        return dto;
    }

    /**
     * Обработчик POST-запроса по маршруту /task_statuses.
     * @param data - запрос на создание статуса задачи в формате TaskStatusCreateDTO
     * @return - возвращает созданный статус задачи в формате ResponseEntity.
     */
    @PostMapping(path = "/task_statuses")
    @ResponseStatus(HttpStatus.CREATED)
    //@PreAuthorize("isAuthenticated()")
    public TaskStatusDTO createTaskStatus(@RequestBody @Valid TaskStatusCreateDTO data) {
        var taskStatus = mapper.map(data);
        repository.save(taskStatus);
        var dto = mapper.map(taskStatus);
        //return ResponseEntity.created(URI.create("/task_statuses"))
        //        .contentType(MediaType.APPLICATION_JSON)
        //        .body(dto);
        return dto;
    }

    /**
     * Обработчик PUT-запроса по маршруту /task_statuses/{id}.
     * @param id - идентификатор статуса
     * @param data - запрос на изменение статуса задачи в TaskUpdateDTO
     * @return - возвращает измененный статус задачи в формате ResponseEntity.
     */
    @PutMapping(path = "/task_statuses/{id}")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("isAuthenticated()")
    public TaskStatusDTO updateTaskStatus(@Valid @RequestBody TaskStatusUpdateDTO data,
                                                          @PathVariable Long id) {
        var taskStatus = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                    "TaskStatus with id " + id + " not found"));
        mapper.update(data, taskStatus);
        repository.save(taskStatus);
        var dto = mapper.map(taskStatus);
        //return ResponseEntity.ok()
        //        .contentType(MediaType.APPLICATION_JSON)
        //        .body(dto);
        return dto;
    }

    /**
     * Обработчик DELETE-запроса по маршруту /task_statuses/{id}.
     * @param id - идентификатор статуса
     */
    @DeleteMapping(path = "/task_statuses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //@PreAuthorize("isAuthenticated()")
    public void deleteTaskStatus(@PathVariable Long id) throws Exception {
        var taskStatus = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "TaskStatus with id " + id + " not found"));
        if (taskStatus.getTasks().size() > 0) {
            throw new IllegalArgumentException("Статус с id " + id + " связан с задачами, поэтому его нельзя удалить");
        } else {
            repository.deleteById(id);
        }
    }
}
