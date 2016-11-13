package xp.librarian.service;

import java.time.*;
import java.time.temporal.*;
import java.util.*;

import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import xp.librarian.model.dto.BookTrace;
import xp.librarian.model.dto.Loan;
import xp.librarian.model.dto.Record;
import xp.librarian.model.dto.User;
import xp.librarian.repository.*;
import xp.librarian.service.mail.MailService;
import xp.librarian.utils.TimeUtils;

/**
 * @author xp
 */
@Service
@Transactional
public class ScheduledService {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledService.class);

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

    @Autowired
    private MailService mailService;

    @Scheduled(fixedDelay = 5 * 1000)
    public void loanApplicationExpiryTick() {
        Instant now = TimeUtils.now();
        List<Loan> loen = loanDao.gets(
                new Loan().setStatus(Loan.Status.APPLYING),
                0L, 0);
        loen.stream()
                .filter(loan -> now.isAfter(loan.getExpiredTime()))
                .forEach(loan -> {
                    if (0 == loanDao.update(
                            new Loan()
                                    .setId(loan.getId())
                                    .setStatus(Loan.Status.APPLYING),
                            new Loan()
                                    .setStatus(Loan.Status.EXPIRED))) {
                        LOG.error(String.format("loan (%d) update failed.", loan.getId()));
                        return;
                    }
                    User user = userDao.get(loan.getUserId());
                    if (user != null) {
                        if (0 == userDao.update(
                                new User().setId(user.getId()),
                                new User().setLoanLimit(user.getLoanLimit() + 1))) {
                            LOG.error(String.format("user (%d) update failed.", loan.getUserId()));
                        }
                    }
                    BookTrace trace = traceDao.get(loan.getTraceId());
                    if (trace != null) {
                        if (0 == traceDao.update(
                                new BookTrace()
                                        .setId(trace.getId())
                                        .setStatus(BookTrace.Status.LOCKED),
                                new BookTrace()
                                        .setStatus(BookTrace.Status.NORMAL)
                                        .setLoanId(0L))) {
                            LOG.info(String.format("book trace (%d) update failed.", trace.getId()));
                            return;
                        }
                    }

                    mailService.noticeExpired(loan);

                    if (0 == recordDao.add(Record.expired(loan))) {
                        throw new PersistenceException("record failed.");
                    }
                });
    }

    @Scheduled(fixedDelay = 60 * 1000)
    public void loanAppointmentExpiryTick() {
        Instant now = TimeUtils.now();
        List<Loan> loen = loanDao.gets(
                new Loan()
                        .setStatus(Loan.Status.ACTIVE)
                        .setIsLate(false),
                0L, 0);
        loen.stream()
                .filter(loan -> now.isAfter(loan.getAppointedTime()))
                .forEach(loan -> {
                    if (0 == loanDao.update(
                            new Loan()
                                    .setId(loan.getId())
                                    .setStatus(Loan.Status.ACTIVE)
                                    .setIsLate(false),
                            new Loan()
                                    .setIsLate(true))) {
                        LOG.info(String.format("loan1 (%d) update failed.", loan.getId()));
                        return;
                    }

                    mailService.noticeLate(loan);

                    if (0 == recordDao.add(Record.expired(loan))) {
                        throw new PersistenceException("record failed.");
                    }
                });
    }

    @Scheduled(fixedDelay = 5 * 1000)
    public void reservationTick() {
        BookTrace where = new BookTrace()
                .setStatus(BookTrace.Status.LOCKED)
                .setLoanId(0L);
        List<BookTrace> traces = traceDao.gets(where, 0L, 0);

        traces.forEach(trace -> {

            Loan loan = loanDao.get(
                    new Loan()
                            .setTraceId(trace.getId())
                            .setStatus(Loan.Status.RESERVING));
            if (loan == null) {
                // 无人预订
                if (0 == traceDao.update(
                        new BookTrace()
                                .setId(trace.getId())
                                .setStatus(BookTrace.Status.LOCKED)
                                .setLoanId(0L),
                        new BookTrace()
                                .setStatus(BookTrace.Status.NORMAL))) {
                    throw new PersistenceException("book trace update failed.");
                }
                return;
            }

            if (0 == traceDao.update(
                    new BookTrace()
                            .setId(trace.getId())
                            .setStatus(BookTrace.Status.LOCKED)
                            .setLoanId(0L),
                    new BookTrace()
                            .setLoanId(loan.getId()))) {
                throw new PersistenceException("book trace update failed.");
            }

            if (0 == loanDao.update(
                    new Loan()
                            .setId(loan.getId())
                            .setStatus(Loan.Status.RESERVING),
                    new Loan()
                            .setStatus(Loan.Status.APPLYING)
                            .setAppointedTime(TimeUtils.afterNow(30L, ChronoUnit.DAYS))
                            .setExpiredTime(TimeUtils.afterNow(24L, ChronoUnit.HOURS)))) {
                throw new PersistenceException("loan update failed.");
            }

            mailService.noticeReserved(loan);

            if (0 == recordDao.add(Record.reserved(loan))) {
                throw new PersistenceException("record failed.");
            }
        });
    }

}
