package hexlet.code.app.service;

//import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
//import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.RequestBody;

//import java.net.URI;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    //@Autowired
    //private PasswordEncoder passwordEncoder;

    /**
     * Метод создания нового пользователя.
     * @param user - на входе объект класса User
     * @return - возвращает созданного пользователя в формате UserDTO.
     */
    public UserDTO create(User user) {

        //var hashedPassword = passwordEncoder.encode(dto.getPassword());
        //user.setPassword(hashedPassword);
        userRepository.save(user);
        var userDTO = userMapper.map(user);
        return userDTO;
    }
}
