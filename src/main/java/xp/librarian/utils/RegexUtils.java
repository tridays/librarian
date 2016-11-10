package xp.librarian.utils;

import java.util.*;
import java.util.regex.*;

import org.apache.commons.lang3.StringUtils;

/**
 * @author xp
 */
public class RegexUtils {

    private static final Pattern bookAuthorPattern = Pattern.compile(
            "#([^#\\s\\f\\r\\n\\t\\v!@%&~`:;\"<>(){}\\^$*+?\\[\\]/\\\\|]+)");

    public static List<String> extractAuthors(String input) {
        if (StringUtils.isEmpty(input)) {
            return Collections.emptyList();
        }
        List<String> authors = new LinkedList<>();
        Matcher matcher = bookAuthorPattern.matcher(input);
        while (matcher.find()) {
            authors.add(matcher.group(1));
        }
        return authors;
    }

}
