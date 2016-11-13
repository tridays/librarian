package xp.librarian.model.form;

import java.io.*;
import java.util.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import xp.librarian.model.context.ValidationException;
import xp.librarian.model.dto.BookTrace;

/**
 * @author xp
 */
@Data
public class BookTraceUpdateForm implements Serializable {

    private static final long serialVersionUID = 5069386085980515997L;

    @ApiModelProperty(hidden = true)
    @NotNull
    private String isbn;

    @ApiModelProperty(hidden = true)
    @NotNull
    private Long traceId;

    private BookTrace.Status status;

    @Length(max = 63325)
    private String location;

    public boolean validate(Validator validator) {
        Set<ConstraintViolation<BookTraceUpdateForm>> vSet = validator.validate(this);
        if (!vSet.isEmpty()) {
            throw new ValidationException(vSet);
        }
        return true;
    }

    public BookTrace forWhere() {
        return new BookTrace()
                .setIsbn(isbn)
                .setId(traceId);
    }

    public BookTrace forSet() {
        return new BookTrace()
                .setStatus(status)
                .setLocation(location);
    }

}
