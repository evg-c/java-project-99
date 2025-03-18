package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsManager {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Метод загружает пользователя по его email.
     * @param email - email пользователя.
     * @return - возвращается найденный пользователь
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));
        return user;
    }

    /**
     * Метод создания пользователя при POST-запросах.
     * @param dto - объект UserCreateDTO, передаваемый при POST-запросах.
     * @return - возвращается объект UserDTO
     */
    public UserDTO createUser(UserCreateDTO dto) {
        var user = userMapper.map(dto);
        userRepository.save(user);
        var userDTO = userMapper.map(user);
        return userDTO;
    }

    // создание пользователя при начальной инициализации
    /**
     * Метод создания пользователя при начальной инициализации.
     * @param userData - объект UserDetails, передаваемый из DataInitializer.
     */
    @Override
    public void createUser(UserDetails userData) {
        var user = new User();
        user.setEmail(userData.getUsername());
        var hashedPassword = passwordEncoder.encode(userData.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    /**
     * Метод не реализован.
     * @param user - объект UserDetails, передаваемый из DataInitializer.
     */
    @Override
    public void updateUser(UserDetails user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateUser'");
    }

    /**
     * Метод не реализован.
     * @param username - имя удаляемого пользователя.
     */
    @Override
    public void deleteUser(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }

    /**
     * Метод не реализован.
     * @param oldPassword - старый пароль.
     * @param newPassword - новый пароль.
     */
    @Override
    public void changePassword(String oldPassword, String newPassword) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
    }

    /**
     * Метод не реализован.
     * @param username - имя проверяемого пользователя.
     */
    @Override
    public boolean userExists(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'userExists'");
    }
}
