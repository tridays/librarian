package xp.librarian.service.mail;

import java.io.*;
import java.util.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import lombok.NonNull;
import xp.librarian.model.dto.Book;
import xp.librarian.model.dto.BookTrace;
import xp.librarian.model.dto.Loan;
import xp.librarian.model.dto.User;
import xp.librarian.repository.BookDao;
import xp.librarian.repository.BookTraceDao;
import xp.librarian.repository.LoanDao;
import xp.librarian.repository.UserDao;

/**
 * @author xp
 */
@Service
public class MailService {

    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserDao userDao;

    @Autowired
    private BookDao bookDao;

    @Autowired
    private BookTraceDao traceDao;

    @Autowired
    private LoanDao loanDao;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String mailFrom;

    public void send(String from, List<String> toList, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message);
            messageHelper.setFrom(from, "Librarian");
            for (String to : toList) {
                messageHelper.addTo(to);
            }
            messageHelper.setSubject(subject);
            messageHelper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            LOG.warn(e.getMessage());
        }
    }

    private String render(String template, Map<String, ?> map) {
        Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariables(map);
        return templateEngine.process(template, context);
    }

    private String getEmail(User user) {
        return Optional.ofNullable(user).map(User::getEmail).orElse(StringUtils.EMPTY);
    }

    private String getEmail(Long userId) {
        return getEmail(userDao.get(userId, true));
    }

    public void noticeExpired(@NonNull Loan loan) {
        User user = userDao.get(loan.getId(), true);
        if (user == null || StringUtils.isEmpty(user.getEmail())) return;
        BookTrace trace = traceDao.get(loan.getTraceId());
        if (trace == null) return;
        Book book = bookDao.get(trace.getIsbn());
        if (book == null) return;

        Map<String, Object> map = new HashMap<>();
        map.put("name", user.getName());
        map.put("body", String.format("Your lending application for %s at %s is expired.", book.getName(), trace.getLocation()));
        send(mailFrom, Collections.singletonList(user.getEmail()),
                "Your Application is Expired",
                render("email/ApplicationNotice", map));
    }

    public void noticeLate(@NonNull Loan loan) {
        User user = userDao.get(loan.getUserId(), true);
        if (user == null || StringUtils.isEmpty(user.getEmail())) return;
        BookTrace trace = traceDao.get(loan.getTraceId());
        if (trace == null) return;
        Book book = bookDao.get(trace.getIsbn());
        if (book == null) return;

        Map<String, Object> map = new HashMap<>();
        map.put("name", user.getName());
        map.put("body", String.format("Your loan for %s is late to return, please return it back as soon as possible.", book.getName()));
        send(mailFrom, Collections.singletonList(user.getEmail()),
                "Your Loan is LATE to return",
                render("email/ApplicationNotice", map));
    }

    public void noticeReserved(@NonNull Loan loan) {
        User user = userDao.get(loan.getId(), true);
        if (user == null || StringUtils.isEmpty(user.getEmail())) return;
        BookTrace trace = traceDao.get(loan.getTraceId());
        if (trace == null) return;
        Book book = bookDao.get(trace.getIsbn());
        if (book == null) return;

        Map<String, Object> map = new HashMap<>();
        map.put("name", user.getName());
        map.put("body", String.format("Your reservation for %s at %s is enabled, please lending it in 24 hours.", book.getName(), trace.getLocation()));
        send(mailFrom, Collections.singletonList(user.getEmail()),
                "Your Reservation is Enabled",
                render("email/ApplicationNotice", map));
    }

}
