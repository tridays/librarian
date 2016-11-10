package xp.librarian.repository.mapper;

import java.util.*;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import xp.librarian.model.dto.BookTrace;

/**
 * @author xp
 */
@Mapper
public interface BookTraceMapper {

    int insert(BookTrace trace);

    int update(@Param("where") BookTrace where,
               @Param("set") BookTrace set);

    List<BookTrace> select(@Param("where") BookTrace where,
                           @Param("offset") Long offset,
                           @Param("limits") Integer limits);

}
