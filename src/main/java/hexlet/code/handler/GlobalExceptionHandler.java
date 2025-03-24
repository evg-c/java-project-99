package hexlet.code.handler;

import hexlet.code.exception.DataIntegrityViolationException;
import hexlet.code.exception.DbException;
import hexlet.code.exception.JdbcSQLIntegrityConstraintViolationException;
import hexlet.code.exception.PSQLException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.exception.SqlExceptionHelper;
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
     * @return - возвращаем ResponseEntity с правильным кодом и телом ответа
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Аннотация @ExceptionHandler указывает, какое исключение обрабатывается проаннотированным ей методом.
     * @param ex - на вход подается возникшее исключение DataIntegrityViolationException
     * @return - возвращаем ResponseEntity с правильным кодом и телом ответа
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Аннотация @ExceptionHandler указывает, какое исключение обрабатывается проаннотированным ей методом.
     * @param ex - на вход подается возникшее исключение DbException
     * @return - возвращаем ResponseEntity с правильным кодом и телом ответа
     */
    @ExceptionHandler(DbException.class)
    public ResponseEntity<String> handleDbException(DbException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Аннотация @ExceptionHandler указывает, какое исключение обрабатывается проаннотированным ей методом.
     * @param ex - на вход подается возникшее исключение PSQLException
     * @return - возвращаем ResponseEntity с правильным кодом и телом ответа
     */
    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<String> handlePSQLException(PSQLException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Аннотация @ExceptionHandler указывает, какое исключение обрабатывается проаннотированным ей методом.
     * @param ex - на вход подается возникшее исключение SqlExceptionHelper
     * @return - возвращаем ResponseEntity с правильным кодом и телом ответа
     */
    @ExceptionHandler(SqlExceptionHelper.class)
    public ResponseEntity<String> handleSqlExceptionHelper(SqlExceptionHelper ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    /**
     * Аннотация @ExceptionHandler указывает, какое исключение обрабатывается проаннотированным ей методом.
     * @param ex - на вход подается возникшее исключение DataIntegrityViolationException
     * @return - возвращаем ResponseEntity с правильным кодом и телом ответа
     */
    @ExceptionHandler(JdbcSQLIntegrityConstraintViolationException.class)
    public ResponseEntity<String>
        handleJdbcSQLIntegrityConstraintViolationException(JdbcSQLIntegrityConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
