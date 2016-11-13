package xp.librarian.model.form;

import java.io.*;
import java.util.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import xp.librarian.model.context.ValidationException;
import xp.librarian.model.dto.BookTrace;

/**
 * @author xp
 */
@Data
public class BookTraceAddForm implements Serializable {

    private static final long serialVersionUID = -4892039389283782045L;

    @ApiModelProperty(hidden = true)
    @NotNull
    @Length(min = 13, max = 13)
    @Pattern(regexp = "[0-9\\-]+")
    private String isbn;

    private BookTrace.Status status;

    @Length(min = 1, max = 63325)
    private String location;

    public boolean validate(Validator validator) {
        Set<ConstraintViolation<BookTraceAddForm>> vSet = validator.validate(this);
        if (!vSet.isEmpty()) {
            throw new ValidationException(vSet);
        }
        return true;
    }

    public BookTrace forSet() {
        return new BookTrace()
                .setIsbn(isbn)
                .setStatus(status)
                .setLocation(location);
    }

}
