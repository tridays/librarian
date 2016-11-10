package xp.librarian.model.form;

import java.io.*;
import java.util.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import xp.librarian.model.context.ValidationException;

/**
 * @author xp
 */
@Data
public class BookSearchForm implements Serializable {

    private static final long serialVersionUID = -9155511439197299065L;

    @Length(max = 20)
    private String isbn;

    @Length(max = 255)
    private String name;

    public boolean validate(Validator validator) {
        Set<ConstraintViolation<BookSearchForm>> vSet = validator.validate(this);
        if (!vSet.isEmpty()) {
            throw new ValidationException(vSet);
        }
        return true;
    }

}
