package xp.librarian.model.form;

import java.io.*;
import java.util.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;
import xp.librarian.model.context.ValidationException;
import xp.librarian.model.dto.User;

/**
 * @author xp
 */
@Data
public class UserLoginForm implements Serializable {

    private static final long serialVersionUID = -5600232455702114345L;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public boolean validate(Validator validator) {
        Set<ConstraintViolation<UserLoginForm>> vSet = validator.validate(this);
        if (!vSet.isEmpty()) {
            throw new ValidationException(vSet);
        }
        return true;
    }

    public User forWhere() {
        return new User()
                .setUsername(username)
                .setPassword(password);
    }

}
