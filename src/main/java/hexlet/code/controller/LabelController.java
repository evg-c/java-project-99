package hexlet.code.controller;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class LabelController {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    /**
     * Обработчик GET-запроса по маршруту /labels.
     * @return - возвращает список меток в формате ResponseEntity.
     */
    @GetMapping(path = "/labels")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LabelDTO>> indexLabels() {
        var labels = labelRepository.findAll();
        var result = labels.stream()
                .map(l -> labelMapper.map(l))
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(result.size()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    /**
     * Обработчик GET-запроса по маршруту /labels/{id}.
     * @param id - идентификатор метки
     * @return - возвращает конкретную метку в формате ResponseEntity.
     */
    @GetMapping(path = "/labels/{id}")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("isAuthenticated()")
    public LabelDTO showLabel(@PathVariable Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        var dto = labelMapper.map(label);
        //return ResponseEntity.ok()
        //        .contentType(MediaType.APPLICATION_JSON)
        //        .body(dto);
        return dto;
    }

    /**
     * Обработчик POST-запроса по маршруту /labels.
     * @param data - запрос на создание метки в формате LabelCreateDTO
     * @return - возвращает созданную метку в формате ResponseEntity.
     */
    @PostMapping(path = "/labels")
    @ResponseStatus(HttpStatus.CREATED)
    //@PreAuthorize("isAuthenticated()")
    public LabelDTO createLabel(@RequestBody @Valid LabelCreateDTO data) {
        var label = labelMapper.map(data);
        labelRepository.save(label);
        var dto = labelMapper.map(label);
        //return ResponseEntity.created(URI.create("/labels"))
        //        .contentType(MediaType.APPLICATION_JSON)
        //        .body(dto);
        return dto;
    }

    /**
     * Обработчик PUT-запроса по маршруту /labels/{id}.
     * @param id - идентификатор метки
     * @param data - запрос на изменение статуса задачи в LabelUpdateDTO
     * @return - возвращает измененный статус задачи в формате ResponseEntity.
     */
    @PutMapping(path = "/labels/{id}")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("isAuthenticated()")
    public LabelDTO updateLabel(@Valid @RequestBody LabelUpdateDTO data,
                                                @PathVariable Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id " + id + " not found"));
        labelMapper.update(data, label);
        labelRepository.save(label);
        var dto = labelMapper.map(label);
        //return ResponseEntity.ok()
        //        .contentType(MediaType.APPLICATION_JSON)
        //        .body(dto);
        return dto;
    }

    /**
     * Обработчик DELETE-запроса по маршруту /labels/{id}.
     * @param id - идентификатор метки
     */
    @DeleteMapping(path = "/labels/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //@PreAuthorize("isAuthenticated()")
    public void deleteLabel(@PathVariable Long id) {
        var label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Label with id " + id + " not found"));
        //if (label.getTasks().size() > 0) {
        //    throw new IllegalArgumentException("Метка с id " + id + " связана с задачами, поэтому ее нельзя удалить");
        //} else {
        //}
        labelRepository.deleteById(id);
    }
}
