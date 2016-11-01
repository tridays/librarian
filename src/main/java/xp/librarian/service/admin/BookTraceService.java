package xp.librarian.service.admin;

import java.util.*;
import java.util.stream.*;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;

import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.NonNull;
import xp.librarian.model.context.BusinessException;
import xp.librarian.model.context.ErrorCode;
import xp.librarian.model.context.ResourceNotFoundException;
import xp.librarian.model.context.ValidationException;
import xp.librarian.model.dto.Book;
import xp.librarian.model.dto.BookTrace;
import xp.librarian.model.dto.Lend;
import xp.librarian.model.form.BookTraceAddForm;
import xp.librarian.model.form.BookTraceUpdateForm;
import xp.librarian.model.form.PagingForm;
import xp.librarian.model.form.UserUpdateForm;
import xp.librarian.model.result.BookTraceVM;
import xp.librarian.repository.BookDao;
import xp.librarian.repository.BookTraceDao;
import xp.librarian.repository.LendDao;
import xp.librarian.repository.UserDao;
import xp.librarian.utils.TimeUtils;

/**
 * @author xp
 */
@Service("adminBookTraceService")
@Transactional
public class BookTraceService {

    @Autowired
    private Validator validator;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BookDao bookDao;

    @Autowired
    private BookTraceDao traceDao;

    @Autowired
    private LendDao lendDao;

    private BookTraceVM buildTraceVM(BookTrace trace) {
        Book book = bookDao.get(trace.getIsbn(), true);
        if (book == null) {
            throw new ResourceNotFoundException("book not found.");
        }
        BookTraceVM vm = new BookTraceVM()
                .withTrace(trace)
                .withBook(book);
        Lend lend = Optional.ofNullable(trace.getLendId()).map(lendDao::get).orElse(null);
        if (lend != null) {
            vm.withLend(lend, Optional.ofNullable(lend.getUserId())
                    .map(userId -> userDao.get(userId, true)).orElse(null));
        }
        return vm;
    }

    public BookTraceVM addTrace(@Valid BookTraceAddForm form) {
        Set<ConstraintViolation<BookTraceAddForm>> vSet = validator.validate(form);
        if (!vSet.isEmpty()) {
            throw new ValidationException(vSet);
        }
        Book book = bookDao.get(form.getIsbn(), true);
        if (book == null) {
            throw new ResourceNotFoundException("book not found.");
        }
        BookTrace trace = form.toDTO();
        if (trace.getStatus() == null) {
            trace.setStatus(BookTrace.Status.NORMAL);
        }
        trace.setCreateTime(TimeUtils.now());
        if (0 == traceDao.add(trace)) {
            throw new PersistenceException("book trace insert failed.");
        }
        return buildTraceVM(trace);
    }

    public BookTraceVM updateTrace(@Valid BookTraceUpdateForm form) {
        Set<ConstraintViolation<BookTraceUpdateForm>> vSet = validator.validate(form);
        if (!vSet.isEmpty()) {
            throw new ValidationException(vSet);
        }
        BookTrace trace = traceDao.get(form.getTraceId(), true);
        if (trace == null) {
            throw new ResourceNotFoundException("book trace not found.");
        }
        if (!trace.getIsbn().equals(form.getIsbn())) {
            throw new InputMismatchException("isbn mismatch.");
        }
        BookTrace where = new BookTrace()
                .setId(trace.getId())
                .setStatus(trace.getStatus());
        BookTrace set = form.toDTO();
        if (0 == traceDao.update(where, set)) {
            throw new PersistenceException("book trace update failed.");
        }
        return buildTraceVM(traceDao.get(trace.getId(), true));
    }

    public void deleteTrace(@NonNull String isbn,
                            @NonNull Integer traceId) {
        BookTrace trace = traceDao.get(traceId, true);
        if (trace == null) {
            throw new ResourceNotFoundException("book trace not found.");
        }
        if (!trace.getIsbn().equals(isbn)) {
            throw new InputMismatchException("isbn not match.");
        }
        if (!BookTrace.Status.NORMAL.equals(trace.getStatus())) {
            throw new BusinessException(ErrorCode.ADMIN_BOOK_TRACE_STATUS_MISMATCH);
        }
        BookTrace where = new BookTrace()
                .setId(trace.getId())
                .setStatus(BookTrace.Status.NORMAL);
        BookTrace set = new BookTrace()
                .setStatus(BookTrace.Status.DELETED);
        if (0 == traceDao.update(where, set)) {
            throw new PersistenceException("book trace update failed.");
        }
    }

    public List<BookTraceVM> getTraces(@NonNull String isbn,
                                       @Valid PagingForm paging) {
        Book book = bookDao.get(isbn, true);
        if (book == null) {
            throw new ResourceNotFoundException("book not found.");
        }
        BookTrace where = new BookTrace()
                .setIsbn(isbn);
        List<BookTrace> traces = traceDao.gets(where, paging.getPage(), paging.getLimits(), true);
        return traces.stream()
                .filter(e -> e != null)
                .distinct()
                .map(this::buildTraceVM)
                .collect(Collectors.toList());
    }

    public BookTraceVM getTrace(@NonNull String isbn,
                                @NonNull Integer traceId) {
        BookTrace trace = traceDao.get(traceId, true);
        if (trace == null) {
            throw new ResourceNotFoundException("book trace not found.");
        }
        if (!trace.getIsbn().equals(isbn)) {
            throw new InputMismatchException("isbn not match.");
        }
        return buildTraceVM(trace);
    }

}
