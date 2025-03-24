package hexlet.code.handler;

import hexlet.code.exception.ResourceNotFoundException;
import org.postgresql.util.PSQLException;
import org.springframework.dao.DataIntegrityViolationException;
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
     * @param ex - на вход подается возникшее исключение ResourceNotFoundException
     * @return - возвращаем ResponseEntity с кодом NOT_FOUND и телом ответа
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Аннотация @ExceptionHandler указывает, какое исключение обрабатывается проаннотированным ей методом.
     * @param ex - на вход подается возникшее исключение DataIntegrityViolationException
     * @return - возвращаем ResponseEntity с кодом CONFLICT и телом ответа
     */
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Нарушение целостности данных в проекте TaskManager: " + "\n"
                        + ex.getMessage());
    }

    /**
     * Аннотация @ExceptionHandler указывает, какое исключение обрабатывается проаннотированным ей методом.
     * @param ex - на вход подается возникшее исключение PSQLException
     * @return - возвращаем ResponseEntity с кодом CONFLICT и телом ответа
     */
    @ExceptionHandler(org.postgresql.util.PSQLException.class)
    public ResponseEntity<String> handlePSQLException(PSQLException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Нарушение целостности данных в проекте TaskManager: " + "\n"
                        + ex.getMessage());
    }
}
