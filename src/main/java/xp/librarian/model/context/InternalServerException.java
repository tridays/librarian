package xp.librarian.model.context;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author xp
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerException extends RuntimeException {

    private static final long serialVersionUID = -7156163369713202580L;

    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException(Throwable cause) {
        super(cause);
    }

    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
