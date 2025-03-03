package hexlet.code.app.util;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userUtils")
@AllArgsConstructor
public class UserUtils {

    @Autowired
    private UserRepository userRepository;

    /**
     * Метод возвращающий текущего пользователя на основе аутентификации Spring Security.
     * @return - возвращает найденного пользователя (или null)
     */
    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        var email = authentication.getName();
        return userRepository.findByEmail(email).get();
    }

    /**
     * Метод определяющий, является ли указанный пользователь текущим.
     * @param userId - идентификатор исследуемого пользователя
     * @return - возвращает найденного пользователя (или null)
     */
    public boolean isCurrentUser(long userId) {
        var userEmail = userRepository.findById(userId).get().getUsername();
        var authenticationEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userEmail.equals(authenticationEmail);
    }
}
