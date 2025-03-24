package hexlet.code.controller;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.JsonNullableMapper;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailService;
import hexlet.code.util.UserUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class UsersController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JsonNullableMapper jsonNullableMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailService userService;

    @Autowired
    private UserUtils userUtils;

    /**
     * Обработчик GET-запроса по маршруту /users.
     * @return - возвращает список пользователей в формате ResponseEntity.
     */
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<UserDTO>> indexOfUsers() {
        var users = userRepository.findAll();
        var result = users.stream()
                .map(user -> userMapper.map(user))
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(result.size()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    /**
     * Обработчик GET-запроса по маршруту /users/{id}.
     * @param id - идентификатор пользователя
     * @return - возвращает конкретного пользователя в формате ResponseEntity.
     */
    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO showUser(@PathVariable Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        var userDTO = userMapper.map(user);
        //return ResponseEntity.ok()
        //        .contentType(MediaType.APPLICATION_JSON)
        //        .body(userDTO);
        return userDTO;
    }

    /**
     * Обработчик POST-запроса по маршруту /users.
     * @param dto - запрос на создание пользователя в формате UserCreateDTO
     * @return - возвращает созданного пользователя в формате ResponseEntity.
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@Valid @RequestBody UserCreateDTO dto) {
        var userDTO = userService.createUser(dto);
        //return ResponseEntity.created(URI.create("/users"))
        //        .contentType(MediaType.APPLICATION_JSON)
        //        .body(userDTO);
        return userDTO;
    }

    /**
     * Обработчик PUT-запроса по маршруту /users/{id}.
     * @param id - идентификатор пользователя
     * @param dto - запрос на изменение пользователя в UserUpdateDTO
     * @return - возвращает измененного пользователя в формате ResponseEntity.
     */
    @PutMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@userUtils.isCurrentUser(#id)")
    public UserDTO updateUser(@RequestBody @Valid UserUpdateDTO dto, @PathVariable Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
//        String hashedPassword = null;
//        if (jsonNullableMapper.isPresent(dto.getPassword())) {
//            hashedPassword = passwordEncoder.encode(jsonNullableMapper.unwrap(dto.getPassword()));
//        }
        userMapper.update(dto, user);
//        if (hashedPassword != null) {
//            user.setPassword(hashedPassword);
//        }
        userRepository.save(user);
        var userDTO = userMapper.map(user);
        //return ResponseEntity.ok()
        //        .contentType(MediaType.APPLICATION_JSON)
        //        .body(userDTO);
        return userDTO;
    }

    /**
     * Обработчик DELETE-запроса по маршруту /users/{id}.
     * @param id - идентификатор пользователя
     */
    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@userUtils.isCurrentUser(#id)")
    public void deleteUser(@PathVariable Long id) throws Exception {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        if (user.getTasks().size() > 0) {
            throw new IllegalArgumentException("User с id " + id + " связан с задачами, поэтому его нельзя удалить");
        }
        userRepository.deleteById(id);
    }
}
