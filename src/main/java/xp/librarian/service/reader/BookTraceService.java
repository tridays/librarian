package xp.librarian.service.reader;

import java.util.*;
import java.util.stream.*;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.NonNull;
import xp.librarian.model.context.ResourceNotFoundException;
import xp.librarian.model.dto.Book;
import xp.librarian.model.dto.BookTrace;
import xp.librarian.model.dto.Loan;
import xp.librarian.model.form.PagingForm;
import xp.librarian.model.result.BookTraceVM;
import xp.librarian.repository.BookDao;
import xp.librarian.repository.BookTraceDao;
import xp.librarian.repository.LoanDao;
import xp.librarian.repository.UserDao;

/**
 * @author xp
 */
@Service("readerBookTraceService")
@Transactional
public class BookTraceService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private BookDao bookDao;

    @Autowired
    private BookTraceDao traceDao;

    @Autowired
    private LoanDao loanDao;

    private Boolean checkReservable(BookTrace trace) {
        if (trace.getStatus() != BookTrace.Status.BORROWED) {
            return false;
        }
        Loan reservation = loanDao.get(new Loan()
                .setTraceId(trace.getId())
                .setStatus(Loan.Status.RESERVING));
        return reservation == null;
    }

    private BookTraceVM buildTraceVM(@NonNull BookTrace trace) {
        Book book = bookDao.get(trace.getIsbn());
        if (book == null) {
            throw new ResourceNotFoundException("book not found.");
        }
        BookTraceVM vm = new BookTraceVM()
                .withTrace(trace)
                .withBook(book);
        Loan loan = Optional.ofNullable(trace.getLoanId()).map(loanDao::get).orElse(null);
        if (loan != null) {
            vm.withLend(loan, Optional.ofNullable(loan.getUserId())
                    .map(userId -> userDao.get(userId)).orElse(null));
        }
        vm.setIsReservable(checkReservable(trace));
        return vm;
    }

    public List<BookTraceVM> getTraces(@NonNull String isbn,
                                       @Valid PagingForm paging) {
        Book book = bookDao.get(isbn);
        if (book == null) {
            throw new ResourceNotFoundException("book not found.");
        }
        BookTrace where = new BookTrace()
                .setIsbn(isbn);
        List<BookTrace> traces = traceDao.gets(where, paging.getOffset(), paging.getLimits());
        return traces.stream()
                .filter(e -> e != null)
                .distinct()
                .map(this::buildTraceVM)
                .collect(Collectors.toList());
    }

    public BookTraceVM getTrace(@NonNull String isbn,
                                @NonNull Long traceId) {
        BookTrace trace = traceDao.get(traceId);
        if (trace == null) {
            throw new ResourceNotFoundException("book trace not found.");
        }
        if (!trace.getIsbn().equals(isbn)) {
            throw new InputMismatchException("isbn not match.");
        }
        return buildTraceVM(trace);
    }

}
