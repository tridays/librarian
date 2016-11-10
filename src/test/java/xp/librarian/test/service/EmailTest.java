package xp.librarian.test.service;

import java.util.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import xp.librarian.config.ServiceConfig;
import xp.librarian.service.mail.MailService;

/**
 * @author xp
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ServiceConfig.class})
@ActiveProfiles("test")
public class EmailTest {

    private static final Logger LOG = LoggerFactory.getLogger(EmailTest.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private MailService mailService;

    @Value("${spring.mail.username}")
    private String mailFrom;

    private String render(String template, Map<String, ?> map) {
        Context context = new Context(LocaleContextHolder.getLocale(), map);
        return templateEngine.process(template, context);
    }

    @Test
    public void sendTest() throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom(mailFrom);
        messageHelper.setTo("tridays@163.com");
        messageHelper.setSubject("test");
        messageHelper.setText("<p>Hello World.</p>", true);
        mailSender.send(message);
    }

    @Test
    public void lateTest() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "tester");
        map.put("body", "Hello, everybody!");
        String template = "email/ApplicationNotice";
        mailService.send(mailFrom, Collections.singletonList("tridays@163.com"),
                "test for template",
                render(template, map));
    }

    @Test
    public void renderTest() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "tester");
        map.put("body", "Hello, everybody!");
        LOG.info(render("email/ApplicationNotice", map));
    }

}
