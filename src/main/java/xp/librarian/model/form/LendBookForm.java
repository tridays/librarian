package xp.librarian.model.form;

import java.io.*;
import java.time.*;
import java.time.temporal.*;
import java.util.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import xp.librarian.model.context.ValidationException;
import xp.librarian.model.dto.Loan;
import xp.librarian.model.validator.MyFuture;

/**
 * @author xp
 */
@Data
public class LendBookForm implements Serializable {

    private static final long serialVersionUID = -2544070182439216592L;

    @ApiModelProperty(hidden = true)
    @NotBlank
    private String isbn;

    @ApiModelProperty(hidden = true)
    @NotNull
    private Long traceId;

    @MyFuture
    private Long appointedTime;

    public boolean validate(Validator validator) {
        Set<ConstraintViolation<LendBookForm>> vSet = validator.validate(this);
        if (!vSet.isEmpty()) {
            throw new ValidationException(vSet);
        }
        return true;
    }

    public Loan forSet() {
        Loan loan = new Loan();
        loan.setTraceId(traceId);

        Instant now = Instant.now();
        Instant appointed;
        if (appointedTime != null) {
            appointed = Instant.ofEpochMilli(appointedTime);
        } else {
            appointed = now.plus(30L, ChronoUnit.DAYS);
        }
        loan.setAppointedDuration((int) ((appointed.toEpochMilli() - now.toEpochMilli()) / 1000));
        loan.setAppointedTime(appointed);
        return loan;
    }

}
