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
import xp.librarian.model.dto.Loan;

/**
 * @author xp
 */
@Data
public class ReserveBookForm implements Serializable {

    private static final long serialVersionUID = 8475848013383572700L;

    @ApiModelProperty(hidden = true)
    @NotNull
    @Length(min = 13, max = 13)
    @Pattern(regexp = "[0-9\\-]+")
    private String isbn;

    @ApiModelProperty(hidden = true)
    @NotNull
    private Long traceId;

    public boolean validate(Validator validator) {
        Set<ConstraintViolation<ReserveBookForm>> vSet = validator.validate(this);
        if (!vSet.isEmpty()) {
            throw new ValidationException(vSet);
        }
        return true;
    }

    public Loan forSet() {
        return new Loan()
                .setTraceId(traceId);
    }

}
