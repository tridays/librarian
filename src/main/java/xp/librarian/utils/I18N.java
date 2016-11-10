package xp.librarian.utils;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * @author xp
 */
public class I18N {

    public static MessageSourceAccessor messageSourceAccessor;

    public static String get(String key) {
        return messageSourceAccessor.getMessage(key, LocaleContextHolder.getLocale());
    }

    public static String get(String key, Object[] values) {
        return messageSourceAccessor.getMessage(key, values, LocaleContextHolder.getLocale());
    }

}
