package xp.librarian.model.form;

import java.io.*;
import java.util.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import lombok.Data;
import xp.librarian.model.context.ValidationException;

/**
 * @author xp
 */
@Data
public class BookSearchForm implements Serializable {

    private static final long serialVersionUID = -9155511439197299065L;

    private String isbn;

    private String name;

    public boolean validate(Validator validator) {
        Set<ConstraintViolation<BookSearchForm>> vSet = validator.validate(this);
        if (!vSet.isEmpty()) {
            throw new ValidationException(vSet);
        }
        return true;
    }

}
