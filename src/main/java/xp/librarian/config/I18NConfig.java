package xp.librarian.config;

import java.util.*;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;

import xp.librarian.component.MyMessageSource;
import xp.librarian.model.context.ErrorCode;
import xp.librarian.utils.I18N;

/**
 * @author xp
 */
@Configuration
public class I18NConfig {

    @Bean
    public MessageSource messageSource() {
        MyMessageSource messageSource = new MyMessageSource();

        Map<String, String> map = new HashMap<>();

        map.put(ErrorCode.ADMIN_BOOK_EXISTS.getKey(), "book exists.");

        map.put(ErrorCode.ADMIN_USER_STATUS_MISMATCH.getKey(), "user cannot be handled now.");
        map.put(ErrorCode.ADMIN_BOOK_STATUS_MISMATCH.getKey(), "book cannot be handled now.");
        map.put(ErrorCode.ADMIN_BOOK_TRACE_STATUS_MISMATCH.getKey(), "book trace cannot be handled now.");
        map.put(ErrorCode.ADMIN_LOAN_STATUS_MISMATCH.getKey(), "loan cannot be handled now.");

        map.put(ErrorCode.USER_EXISTS.getKey(), "user exists.");
        map.put(ErrorCode.USER_LOGIN_FAIL.getKey(), "login failed.");

        map.put(ErrorCode.BOOK_TRACE_STATUS_MISMATCH.getKey(), "book trace cannot be handled now.");
        map.put(ErrorCode.LOAN_STATUS_MISMATCH.getKey(), "loan cannot be handled now.");

        map.put(ErrorCode.LOAN_USER_REMAIN_NO_LOAN_LIMIT.getKey(), "reach the lend limit.");
        map.put(ErrorCode.LOAN_LENDER_EQUALS_RESERVATION_APPLICANT.getKey(), "lender equals reservation applicant.");
        map.put(ErrorCode.LOAN_RESERVATION_EXISTS.getKey(), "book trace has been reserved.");
        map.put(ErrorCode.LOAN_REACH_MAX_RENEW_COUNT.getKey(), "reach the max renew count.");

        messageSource.addMessages(map, messageSource.getDefaultLocale());
        return messageSource;
    }

    @Bean
    public MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {
        MessageSourceAccessor accessor = new MessageSourceAccessor(messageSource);
        I18N.messageSourceAccessor = accessor;
        return accessor;
    }

}
