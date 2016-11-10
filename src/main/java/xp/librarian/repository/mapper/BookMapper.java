package xp.librarian.repository.mapper;

import java.util.*;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import xp.librarian.model.dto.Book;

/**
 * @author xp
 */
@Mapper
public interface BookMapper {

    int insert(Book book);

    int update(@Param("where") Book where,
               @Param("set") Book set);

    List<Book> select(@Param("where") Book where,
                      @Param("offset") Long offset,
                      @Param("limits") Integer limits);

    List<Book> selectIN(@Param("isbns") Collection<String> isbns);

    List<Book> search(@Param("where") Book where,
                      @Param("offset") Long offset,
                      @Param("limits") Integer limits);

}
