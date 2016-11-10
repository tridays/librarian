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
public class BookTraceVM implements Serializable {

    private static final long serialVersionUID = -7116954866052993964L;

    private Long id;

    private String isbn;

    private BookVM book;

    private BookTrace.Status status;

    private String location;

    private Long loanId;

    private LoanVM loan;

    private Long createTime;

    public BookTraceVM withTrace(BookTrace trace) {
        if (trace != null) {
            this.id = trace.getId();
            this.isbn = trace.getIsbn();
            this.status = trace.getStatus();
            this.location = trace.getLocation();
            this.loanId = trace.getLoanId();
            this.createTime = Optional.ofNullable(trace.getCreateTime()).map(Instant::toEpochMilli).orElse(null);
        }
        return this;
    }

    public BookTraceVM withBook(Book book) {
        if (book != null) {
            this.book = new BookVM().withBook(book);
        }
        return this;
    }

    public BookTraceVM withLend(Loan loan, User user) {
        if (loan != null) {
            this.loan = new LoanVM().withLoan(loan).withUser(user);
        }
        return this;
    }

}
