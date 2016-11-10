package xp.librarian.repository;

import java.util.*;

import xp.librarian.model.dto.BookTrace;

/**
 * @author xp
 */
public interface BookTraceDao {

    int add(BookTrace trace);

    int update(BookTrace where, BookTrace set);

    BookTrace get(Long traceId);

    BookTrace get(Long traceId, boolean force);

    List<BookTrace> gets(BookTrace where, Long offset, Integer limits);

    List<BookTrace> gets(BookTrace where, Long offset, Integer limits, boolean force);

}
