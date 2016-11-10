package xp.librarian.model.result;

import java.io.*;
import java.time.*;
import java.util.*;

import lombok.Data;
import xp.librarian.model.dto.Book;
import xp.librarian.model.dto.BookTrace;
import xp.librarian.model.dto.Loan;
import xp.librarian.model.dto.User;

/**
 * @author xp
 */
@Data
public class LoanVM implements Serializable {

    private static final long serialVersionUID = 6784313847752988388L;

    private Long id;

    private Long userId;

    private UserVM user;

    private Long traceId;

    private BookTraceVM trace;

    private Loan.Status status;

    private Boolean isReservation;

    private Boolean isLate;

    private Integer renewCount;

    private Long applyingTime;

    private Long activeTime;

    private Long appointedTime;

    private Long expiredTime;

    public LoanVM withLoan(Loan loan) {
        if (loan != null) {
            this.id = loan.getId();
            this.userId = loan.getUserId();
            this.traceId = loan.getTraceId();
            this.status = loan.getStatus();
            this.isReservation = loan.getIsReservation();
            this.isLate = loan.getIsLate();
            this.renewCount = loan.getRenewCount();
            this.applyingTime = Optional.ofNullable(loan.getCreateTime()).map(Instant::toEpochMilli).orElse(null);
            this.activeTime = Optional.ofNullable(loan.getActiveTime()).map(Instant::toEpochMilli).orElse(null);
            this.appointedTime = Optional.ofNullable(loan.getAppointedTime()).map(Instant::toEpochMilli).orElse(null);
            this.expiredTime = Optional.ofNullable(loan.getExpiredTime()).map(Instant::toEpochMilli).orElse(null);
        }
        return this;
    }

    public LoanVM withUser(User user) {
        if (user != null) {
            this.user = new UserVM().withUser(user);
        }
        return this;
    }

    public LoanVM withTrace(BookTrace trace, Book book) {
        if (trace != null) {
            this.trace = new BookTraceVM().withTrace(trace).withBook(book);
        }
        return this;
    }

}
