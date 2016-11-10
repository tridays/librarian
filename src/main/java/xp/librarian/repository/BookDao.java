package xp.librarian.repository;

import java.util.*;

import xp.librarian.model.dto.Book;

/**
 * @author xp
 */
public interface BookDao {

    int add(Book book);

    int update(Book where, Book set);

    Book get(String isbn);

    Book get(String isbn, boolean force);

    List<Book> gets(Book where, Long offset, Integer limits);

    List<Book> gets(Book where, Long offset, Integer limits, boolean force);

    List<Book> search(Book book, Long offset, Integer limits);

    List<Book> search(Book book, Long offset, Integer limits, boolean force);

}
