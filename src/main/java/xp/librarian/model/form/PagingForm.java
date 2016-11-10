package xp.librarian.model.form;

import java.io.*;
import java.util.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Range;

import lombok.Data;
import xp.librarian.model.context.ValidationException;

/**
 * @author xp
 */
@Data
public class PagingForm implements Serializable {

    private static final long serialVersionUID = 6992199899328251070L;

    @Min(1L)
    private long page = 1L;

    @Range(min = 0L, max = 256L)
    private int limits = 20;

    public boolean validate(Validator validator) {
        Set<ConstraintViolation<PagingForm>> vSet = validator.validate(this);
        if (!vSet.isEmpty()) {
            throw new ValidationException(vSet);
        }
        return true;
    }

    public long getOffset() {
        return (page - 1) * limits;
    }

}
