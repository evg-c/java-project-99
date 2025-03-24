package hexlet.code.exception;

import jakarta.annotation.Nullable;
import org.postgresql.util.PSQLState;

public class PSQLException extends org.postgresql.util.PSQLException {
    public PSQLException(String message, @Nullable String msg, @Nullable PSQLState state, @Nullable Throwable cause) {
        super(message + msg, state, cause);
    }
}
