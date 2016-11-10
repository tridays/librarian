package xp.librarian.component;

import java.text.*;
import java.util.*;

import org.springframework.context.support.StaticMessageSource;

import lombok.Getter;

/**
 * @author xp
 */
public class MyMessageSource extends StaticMessageSource {

    @Getter
    private Locale defaultLocale;

    public MyMessageSource() {
        this.defaultLocale = Locale.ENGLISH;
    }

    public MyMessageSource(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        String result = super.resolveCodeWithoutArguments(code, locale);
        if (result == null) {
            result = super.resolveCodeWithoutArguments(code, new Locale(locale.getLanguage()));
            if (result == null) {
                result = super.resolveCodeWithoutArguments(code, defaultLocale);
            }
        }
        return result;
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        MessageFormat result = super.resolveCode(code, locale);
        if (result == null) {
            result = super.resolveCode(code, new Locale(locale.getLanguage()));
            if (result == null) {
                result = super.resolveCode(code, defaultLocale);
            }
        }
        return result;
    }

    @Override
    protected String getDefaultMessage(String code) {
        return resolveCodeWithoutArguments(code, defaultLocale);
    }

}
