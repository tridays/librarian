package xp.librarian.service.reader;

import java.time.temporal.*;
import java.util.*;
import java.util.stream.*;

import javax.validation.Valid;
import javax.validation.Validator;

import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.NonNull;
import xp.librarian.model.context.*;
import xp.librarian.model.dto.*;
import xp.librarian.model.form.LendBookForm;
import xp.librarian.model.form.LendListForm;
import xp.librarian.model.form.PagingForm;
import xp.librarian.model.form.ReserveBookForm;
import xp.librarian.model.result.LoanVM;
import xp.librarian.repository.*;
import xp.librarian.utils.TimeUtils;

/**
 * @author xp
 */
@Service("readerLoanService")
@Transactional
public class LoanService {

    @Autowired
    private Validator validator;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BookTraceDao traceDao;

    @Autowired
    private BookDao bookDao;

    @Autowired
    private LoanDao loanDao;

    @Autowired
    private RecordDao recordDao;

    private LoanVM buildLoanVM(@NonNull Loan loan) {
        User user = userDao.get(loan.getUserId());
        if (user == null) {
            throw new ResourceNotFoundException("user not found.");
        }
        BookTrace trace = traceDao.get(loan.getTraceId());
        if (trace == null) {
            throw new ResourceNotFoundException("book trace not found.");
        }
        Book book = bookDao.get(trace.getIsbn());
        if (book == null) {
            throw new ResourceNotFoundException("book not found.");
        }
        return new LoanVM().withLoan(loan).withUser(user).withTrace(trace, book);
    }

    public LoanVM lendBook(@NonNull AccountContext account,
                           @Valid LendBookForm form) {
        form.validate(validator);

        BookTrace trace = traceDao.get(form.getTraceId());
        if (trace == null) {
            throw new ResourceNotFoundException("book trace not found.");
        }
        if (!trace.getIsbn().equals(form.getIsbn())) {
            throw new InputMismatchException("isbn not match.");
        }
        if (!BookTrace.Status.NORMAL.equals(trace.getStatus())) {
            throw new BusinessException(ErrorCode.BOOK_TRACE_STATUS_MISMATCH);
        }

        Loan loan = form.forSet()
                .setUserId(account.getId())
                .setStatus(Loan.Status.APPLYING)
                .setCreateTime(TimeUtils.now())
                .setExpiredTime(TimeUtils.afterNow(2L, ChronoUnit.HOURS));
        if (0 == loanDao.add(loan)) {
            throw new PersistenceException("loan insert failed.");
        }

        BookTrace where = new BookTrace()
                .setId(trace.getId())
                .setStatus(BookTrace.Status.NORMAL);
        BookTrace set = new BookTrace()
                .setStatus(BookTrace.Status.LOCKED)
                .setLoanId(loan.getId());
        if (0 == traceDao.update(where, set)) {
            throw new PersistenceException("book trace update failed.");
        }

        if (0 == recordDao.add(Record.apply(loan))) {
            throw new PersistenceException("record failed.");
        }

        return buildLoanVM(loan);
    }

    public List<LoanVM> getLoen(@NonNull AccountContext account,
                                @Valid LendListForm form,
                                @Valid PagingForm paging) {
        Loan where = new Loan()
                .setUserId(account.getId())
                .setStatus(form.getStatus());
        List<Loan> loen = loanDao.gets(where, paging.getOffset(), paging.getLimits());
        return loen.stream()
                .filter(e -> e != null)
                .distinct()
                .map(this::buildLoanVM)
                .collect(Collectors.toList());
    }

    public LoanVM getLoan(@NonNull AccountContext account,
                          @NonNull Long loanId) {
        Loan loan = loanDao.get(loanId);
        if (loan == null) {
            throw new ResourceNotFoundException("loan not found.");
        }
        if (!loan.getUserId().equals(account.getId())) {
            throw new AccessForbiddenException("access denied.");
        }
        return buildLoanVM(loan);
    }

    public void cancelLending(@NonNull AccountContext account,
                              @NonNull Long loanId) {
        Loan loan = loanDao.get(loanId);
        if (loan == null) {
            throw new ResourceNotFoundException("loan not found.");
        }
        if (!loan.getUserId().equals(account.getId())) {
            throw new AccessForbiddenException("access denied.");
        }
        Loan where = new Loan()
                .setId(loan.getId())
                .setStatus(Loan.Status.APPLYING);
        Loan set = new Loan()
                .setStatus(Loan.Status.CANCELED);
        if (0 == loanDao.update(where, set)) {
            throw new PersistenceException("loan update failed.");
        }

        if (0 == recordDao.add(Record.cancelApplication(loan))) {
            throw new PersistenceException("record failed.");
        }
    }

    public LoanVM renewLending(@NonNull AccountContext account,
                               @NonNull Long loanId) {
        Loan loan = loanDao.get(loanId);
        if (loan == null) {
            throw new ResourceNotFoundException("loan not found.");
        }
        if (!loan.getUserId().equals(account.getId())) {
            throw new AccessForbiddenException("access denied.");
        }
        if (!Loan.Status.ACTIVE.equals(loan.getStatus())) {
            throw new BusinessException(ErrorCode.LOAN_STATUS_MISMATCH);
        }
        if (loan.getRenewCount() != 0) {
            throw new BusinessException(ErrorCode.LOAN_REACH_MAX_RENEW_COUNT);
        }
        Loan where = new Loan()
                .setId(loan.getId())
                .setStatus(Loan.Status.ACTIVE)
                .setRenewCount(loan.getRenewCount())
                .setAppointedTime(loan.getAppointedTime());
        Loan set = new Loan()
                .setRenewCount(loan.getRenewCount() + 1)
                .setAppointedTime(TimeUtils.after(loan.getAppointedTime(), 30L, ChronoUnit.DAYS));
        if (0 == loanDao.update(where, set)) {
            throw new PersistenceException("loan update failed.");
        }

        if (0 == recordDao.add(Record.renewLend(loan))) {
            throw new PersistenceException("record failed.");
        }

        return buildLoanVM(loanDao.get(loanId));
    }

    public LoanVM reserveBook(@NonNull AccountContext account,
                              @Valid ReserveBookForm form) {
        form.validate(validator);

        BookTrace trace = traceDao.get(form.getTraceId());
        if (trace == null) {
            throw new ResourceNotFoundException("book trace not found.");
        }
        if (!trace.getIsbn().equals(form.getIsbn())) {
            throw new InputMismatchException("isbn not match.");
        }
        if (!BookTrace.Status.BORROWED.equals(trace.getStatus())) {
            throw new BusinessException(ErrorCode.BOOK_TRACE_STATUS_MISMATCH);
        }
        Loan where = new Loan()
                .setTraceId(trace.getId())
                .setStatus(Loan.Status.RESERVING);
        Loan existReservation = loanDao.get(where);
        if (existReservation != null) {
            throw new BusinessException(ErrorCode.LOAN_RESERVATION_EXISTS);
        }
        Loan loan = form.forSet()
                .setUserId(account.getId())
                .setStatus(Loan.Status.RESERVING)
                .setIsReservation(true)
                .setCreateTime(TimeUtils.now());
        if (0 == loanDao.add(loan)) {
            throw new PersistenceException("loan insert failed.");
        }

        if (0 == recordDao.add(Record.reserve(loan))) {
            throw new PersistenceException("record failed.");
        }

        return buildLoanVM(loan);
    }

    public void cancelReservation(@NonNull AccountContext account,
                                  @NonNull Long loanId) {
        Loan loan = loanDao.get(loanId);
        if (loan == null) {
            throw new ResourceNotFoundException("loan not found.");
        }
        if (!loan.getUserId().equals(account.getId())) {
            throw new AccessForbiddenException("access denied.");
        }
        Loan where = new Loan()
                .setId(loan.getId())
                .setStatus(Loan.Status.RESERVING);
        Loan set = new Loan()
                .setStatus(Loan.Status.CANCELED);
        if (0 == loanDao.update(where, set)) {
            throw new PersistenceException("loan update failed.");
        }

        if (0 == recordDao.add(Record.cancelReservation(loan))) {
            throw new PersistenceException("record failed.");
        }
    }

}
