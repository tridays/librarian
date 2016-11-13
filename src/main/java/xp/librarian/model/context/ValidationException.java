package xp.librarian.model.context;

import java.util.*;

import javax.validation.ConstraintViolation;

import lombok.Getter;

/**
 * @author xp
 */
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 3910454411201282029L;

    @Getter
    private Set<Object> messages;

    public <T> ValidationException(Set<ConstraintViolation<T>> violations) {
        messages = new HashSet<>(violations.size());
        violations.forEach(e -> {
            HashMap<String, Object> one = new HashMap<>();
            one.put("field", e.getPropertyPath().toString());
            one.put("value", e.getInvalidValue());
            one.put("message", e.getMessage());
            messages.add(one);
        });
    }

    @Override
    public Throwable fillInStackTrace() {
        return null;
    }
}
