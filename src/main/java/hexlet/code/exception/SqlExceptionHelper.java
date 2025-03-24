package hexlet.code.exception;

public class SqlExceptionHelper extends RuntimeException {
    public SqlExceptionHelper(String message) {
        super(message);
    }
}
