package xp.librarian.service.admin;

import java.time.*;
import java.util.*;
import java.util.stream.*;

import javax.validation.Valid;
import javax.validation.Validator;

import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.NonNull;
import xp.librarian.model.context.BusinessException;
import xp.librarian.model.context.ErrorCode;
import xp.librarian.model.context.InternalServerException;
import xp.librarian.model.context.ResourceNotFoundException;
import xp.librarian.model.dto.*;
import xp.librarian.model.form.AdminLoanListForm;
import xp.librarian.model.form.AdminQuickLendForm;
import xp.librarian.model.form.PagingForm;
import xp.librarian.model.result.LoanVM;
import xp.librarian.repository.*;
import xp.librarian.utils.TimeUtils;

/**
 * @author xp
 */
@Service("adminLoanService")
@Transactional
public class LoanService {

    @Autowired
    private Validator validator;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BookDao bookDao;

    @Autowired
    private BookTraceDao traceDao;

    @Autowired
    private LoanDao loanDao;

    @Autowired
    private RecordDao recordDao;

    private LoanVM buildLoanVM(@NonNull Loan loan) {
        User user = userDao.get(loan.getUserId(), true);
        if (user == null) {
            throw new ResourceNotFoundException("user not found.");
        }
        BookTrace trace = traceDao.get(loan.getTraceId(), true);
        if (trace == null) {
            throw new ResourceNotFoundException("book trace not found.");
        }
        Book book = bookDao.get(trace.getIsbn(), true);
        if (book == null) {
            throw new ResourceNotFoundException("book not found.");
        }
        return new LoanVM().withLoan(loan).withUser(user).withTrace(trace, book);
    }

    public List<LoanVM> getLoen(@Valid AdminLoanListForm form,
                                @Valid PagingForm paging) {
        List<Loan> loen = loanDao.gets(
                new Loan()
                        .setUserId(form.getUserId())
                        .setStatus(form.getStatus()),
                paging.getOffset(), paging.getLimits());
        return loen.stream()
                .filter(e -> e != null)
                .distinct()
                .map(this::buildLoanVM)
                .collect(Collectors.toList());
    }

    public LoanVM getLoan(@NonNull Long loanId) {
        Loan loan = loanDao.get(loanId);
        if (loan == null) {
            throw new ResourceNotFoundException("loan not found.");
        }
        return buildLoanVM(loan);
    }

    public LoanVM acceptLending(@NonNull Long loanId) {
        Loan loan = loanDao.get(loanId);
        if (loan == null) {
            throw new ResourceNotFoundException("loan not found.");
        }
        if (!Loan.Status.APPLYING.equals(loan.getStatus())) {
            throw new BusinessException(ErrorCode.ADMIN_LOAN_STATUS_MISMATCH);
        }
        BookTrace trace = traceDao.get(loan.getTraceId());
        if (trace == null) {
            throw new ResourceNotFoundException("trace not found.");
        }
        if (!BookTrace.Status.LOCKED.equals(trace.getStatus())) {
            throw new BusinessException(ErrorCode.ADMIN_BOOK_TRACE_STATUS_MISMATCH);
        }
        if (!loan.getId().equals(trace.getLoanId())) {
            throw new InternalServerException("book trace -> loan not matched.");
        }
        if (0 == loanDao.update(
                new Loan()
                        .setId(loanId)
                        .setStatus(Loan.Status.APPLYING),
                new Loan()
                        .setStatus(Loan.Status.ACTIVE)
                        .setActiveTime(Instant.now()))) {
            throw new PersistenceException("loan update failed.");
        }
        if (0 == traceDao.update(
                new BookTrace()
                        .setId(trace.getId())
                        .setStatus(BookTrace.Status.LOCKED)
                        .setLoanId(loan.getId()),
                new BookTrace()
                        .setStatus(BookTrace.Status.BORROWED))) {
            throw new PersistenceException("book trace update failed.");
        }

        if (0 == recordDao.add(Record.accept(loan))) {
            throw new PersistenceException("record failed.");
        }

        return buildLoanVM(loanDao.get(loanId));
    }

    public LoanVM rejectLending(@NonNull Long loanId) {
        Loan loan = loanDao.get(loanId);
        if (loan == null) {
            throw new ResourceNotFoundException("loan not found.");
        }
        if (!Loan.Status.APPLYING.equals(loan.getStatus())) {
            throw new BusinessException(ErrorCode.ADMIN_LOAN_STATUS_MISMATCH);
        }
        BookTrace trace = traceDao.get(loan.getTraceId());
        if (trace == null) {
            throw new ResourceNotFoundException("trace not found.");
        }
        if (!BookTrace.Status.LOCKED.equals(trace.getStatus())) {
            throw new BusinessException(ErrorCode.ADMIN_BOOK_TRACE_STATUS_MISMATCH);
        }
        if (!loan.getId().equals(trace.getLoanId())) {
            throw new InternalServerException("book trace -> loan not matched.");
        }
        if (0 == loanDao.update(
                new Loan()
                        .setId(loanId)
                        .setStatus(Loan.Status.APPLYING),
                new Loan()
                        .setStatus(Loan.Status.REJECTED))) {
            throw new PersistenceException("loan update failed.");
        }
        if (0 == traceDao.update(
                new BookTrace()
                        .setId(trace.getId())
                        .setStatus(BookTrace.Status.LOCKED)
                        .setLoanId(loan.getId()),
                new BookTrace()
                        .setStatus(BookTrace.Status.NORMAL))) {
            throw new PersistenceException("book trace update failed.");
        }

        if (0 == recordDao.add(Record.reject(loan))) {
            throw new PersistenceException("record failed.");
        }

        return buildLoanVM(loanDao.get(loanId));
    }

    public LoanVM confirmReturned(@NonNull Long loanId) {
        Loan loan = loanDao.get(loanId);
        if (loan == null) {
            throw new ResourceNotFoundException("loan not found.");
        }
        if (!Loan.Status.ACTIVE.equals(loan.getStatus())) {
            throw new BusinessException(ErrorCode.ADMIN_LOAN_STATUS_MISMATCH);
        }
        User user = userDao.get(loan.getUserId());
        if (user == null) {
            throw new InternalServerException("user not found.");
        }
        BookTrace trace = traceDao.get(loan.getTraceId());
        if (trace == null) {
            throw new ResourceNotFoundException("trace not found.");
        }
        if (!BookTrace.Status.BORROWED.equals(trace.getStatus())) {
            throw new BusinessException(ErrorCode.ADMIN_BOOK_TRACE_STATUS_MISMATCH);
        }
        if (!loan.getId().equals(trace.getLoanId())) {
            throw new InternalServerException("book trace -> loan not matched.");
        }
        if (0 == loanDao.update(
                new Loan()
                        .setId(loanId)
                        .setStatus(Loan.Status.ACTIVE),
                new Loan()
                        .setStatus(Loan.Status.RETURNED))) {
            throw new PersistenceException("loan update failed.");
        }
        if (0 == traceDao.update(
                new BookTrace()
                        .setId(trace.getId())
                        .setStatus(BookTrace.Status.BORROWED)
                        .setLoanId(loan.getId()),
                new BookTrace()
                        .setStatus(BookTrace.Status.LOCKED)
                        .setLoanId(0L))) {
            throw new PersistenceException("book trace update failed.");
        }
        if (0 == userDao.update(
                new User().setId(user.getId()),
                new User().setLoanLimit(user.getLoanLimit() + 1))) {
            throw new PersistenceException("user update failed.");
        }

        if (0 == recordDao.add(Record.confirmReturned(loan))) {
            throw new PersistenceException("record failed.");
        }

        return buildLoanVM(loanDao.get(loanId));
    }

    public LoanVM confirmDisabled(@NonNull Long loanId) {
        Loan loan = loanDao.get(loanId);
        if (loan == null) {
            throw new ResourceNotFoundException("loan not found.");
        }
        if (!Loan.Status.ACTIVE.equals(loan.getStatus())) {
            throw new BusinessException(ErrorCode.ADMIN_LOAN_STATUS_MISMATCH);
        }
        BookTrace trace = traceDao.get(loan.getTraceId());
        if (trace == null) {
            throw new ResourceNotFoundException("trace not found.");
        }
        if (!BookTrace.Status.BORROWED.equals(trace.getStatus())) {
            throw new BusinessException(ErrorCode.ADMIN_BOOK_TRACE_STATUS_MISMATCH);
        }
        if (!loan.getId().equals(trace.getLoanId())) {
            throw new InternalServerException("book trace -> loan not matched.");
        }
        if (0 == loanDao.update(
                new Loan()
                        .setId(loanId)
                        .setStatus(Loan.Status.ACTIVE),
                new Loan()
                        .setStatus(Loan.Status.DISABLED))) {
            throw new PersistenceException("loan update failed.");
        }
        if (0 == traceDao.update(
                new BookTrace()
                        .setId(trace.getId())
                        .setStatus(BookTrace.Status.BORROWED)
                        .setLoanId(loan.getId()),
                new BookTrace()
                        .setStatus(BookTrace.Status.DELETED))) {
            throw new PersistenceException("book trace update failed.");
        }

        if (0 == recordDao.add(Record.confirmDisabled(loan))) {
            throw new PersistenceException("record failed.");
        }

        return buildLoanVM(loanDao.get(loanId));
    }

    public LoanVM quickLend(@Valid AdminQuickLendForm form) {
        form.validate(validator);

        BookTrace trace = traceDao.get(form.getTraceId(), true);
        if (trace == null) {
            throw new ResourceNotFoundException("book trace not found.");
        }
        if (!trace.getIsbn().equals(form.getIsbn())) {
            throw new InputMismatchException("isbn not match.");
        }
        if (!BookTrace.Status.NORMAL.equals(trace.getStatus())) {
            throw new BusinessException(ErrorCode.BOOK_TRACE_STATUS_MISMATCH);
        }
        User user = userDao.get(new User().setUsername(form.getUsername()));
        if (user == null) {
            throw new ResourceNotFoundException("user not found.");
        }
        if (user.getLoanLimit() < 1) {
            throw new BusinessException(ErrorCode.LOAN_USER_REMAIN_NO_LOAN_LIMIT);
        }

        Instant now = TimeUtils.now();
        Loan loan = form.forSet()
                .setUserId(user.getId())
                .setStatus(Loan.Status.ACTIVE)
                .setCreateTime(now)
                .setActiveTime(now);
        if (0 == loanDao.add(loan)) {
            throw new PersistenceException("loan insert failed.");
        }

        if (0 == traceDao.update(
                new BookTrace()
                        .setId(trace.getId())
                        .setStatus(BookTrace.Status.NORMAL),
                new BookTrace()
                        .setStatus(BookTrace.Status.BORROWED)
                        .setLoanId(loan.getId()))) {
            throw new PersistenceException("book trace update failed.");
        }

        if (0 == userDao.update(
                new User().setId(user.getId()),
                new User().setLoanLimit(user.getLoanLimit() - 1))) {
            throw new PersistenceException("user update failed.");
        }

        if (0 == recordDao.add(Record.quickLending(loan))) {
            throw new PersistenceException("record failed.");
        }

        return buildLoanVM(loan);
    }

    public LoanVM quickReturn(@NonNull String isbn, @NonNull Long traceId) {
        BookTrace trace = traceDao.get(traceId, true);
        if (trace == null) {
            throw new ResourceNotFoundException("book trace not found.");
        }
        if (!trace.getIsbn().equals(isbn)) {
            throw new InputMismatchException("isbn not match.");
        }
        if (!BookTrace.Status.BORROWED.equals(trace.getStatus())) {
            throw new BusinessException(ErrorCode.ADMIN_BOOK_TRACE_STATUS_MISMATCH);
        }
        Loan loan = loanDao.get(trace.getLoanId());
        if (loan == null) {
            throw new ResourceNotFoundException("loan not found.");
        }
        if (!loan.getId().equals(trace.getLoanId())) {
            throw new InternalServerException("book trace -> loan not matched.");
        }
        if (!Loan.Status.ACTIVE.equals(loan.getStatus())) {
            throw new BusinessException(ErrorCode.ADMIN_LOAN_STATUS_MISMATCH);
        }
        User user = userDao.get(loan.getUserId());
        if (user == null) {
            throw new InternalServerException("user not found.");
        }
        if (0 == loanDao.update(
                new Loan()
                        .setId(loan.getId())
                        .setStatus(Loan.Status.ACTIVE),
                new Loan()
                        .setStatus(Loan.Status.RETURNED))) {
            throw new PersistenceException("loan update failed.");
        }
        if (0 == traceDao.update(
                new BookTrace()
                        .setId(trace.getId())
                        .setStatus(BookTrace.Status.BORROWED)
                        .setLoanId(loan.getId()),
                new BookTrace()
                        .setStatus(BookTrace.Status.LOCKED)
                        .setLoanId(0L))) {
            throw new PersistenceException("book trace update failed.");
        }
        if (0 == userDao.update(
                new User().setId(user.getId()),
                new User().setLoanLimit(user.getLoanLimit() + 1))) {
            throw new PersistenceException("user update failed.");
        }

        if (0 == recordDao.add(Record.quickReturn(loan))) {
            throw new PersistenceException("record failed.");
        }

        return buildLoanVM(loanDao.get(loan.getId()));
    }

}
