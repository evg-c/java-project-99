package hexlet.code.exception;

public class DataIntegrityViolationException extends org.springframework.dao.DataIntegrityViolationException {
    public DataIntegrityViolationException(String message) {
        super(message);
    }
}
