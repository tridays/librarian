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
import xp.librarian.model.form.PagingForm;
import xp.librarian.model.result.LoanVM;
import xp.librarian.repository.*;

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
        Loan where = new Loan()
                .setUserId(form.getUserId())
                .setStatus(form.getStatus());
        List<Loan> loen = loanDao.gets(where, paging.getOffset(), paging.getLimits());
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

    public void acceptLending(@NonNull Long loanId) {
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
    }

    public void rejectLending(@NonNull Long loanId) {
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
    }

    public void confirmReturned(@NonNull Long loanId) {
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
                        .setStatus(Loan.Status.RETURNED)
                        .setActiveTime(Instant.now()))) {
            throw new PersistenceException("loan update failed.");
        }
        if (0 == traceDao.update(
                new BookTrace()
                        .setId(trace.getId())
                        .setStatus(BookTrace.Status.BORROWED)
                        .setLoanId(loan.getId()),
                new BookTrace()
                        .setStatus(BookTrace.Status.LOCKED))) {
            throw new PersistenceException("book trace update failed.");
        }

        if (0 == recordDao.add(Record.confirmReturned(loan))) {
            throw new PersistenceException("record failed.");
        }
    }

    public void confirmDisabled(@NonNull Long loanId) {
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
                        .setStatus(Loan.Status.DISABLED)
                        .setActiveTime(Instant.now()))) {
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
    }

}
