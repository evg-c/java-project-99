package hexlet.code.exception;

public class JdbcSQLIntegrityConstraintViolationException extends RuntimeException {
    public JdbcSQLIntegrityConstraintViolationException(String message) {
        super(message);
    }
}
