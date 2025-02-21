package hexlet.code.app.handler;

import hexlet.code.app.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Глобальный обработчик исключений (всего приложения).
 * Аннотация @ControllerAdvice указывает, что класс отвечает за централизованную обработку исключений.
 */

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Аннотация @ExceptionHandler указывает, какое исключение обрабатывается проаннотированным ей методом.
     * @param ex - на вход подается возникшее исключение
     * @return - возвращаем ResponseEntity с правильным кодом и телом ответа
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
