package xp.librarian.service.reader;

import java.util.*;
import java.util.stream.*;

import javax.validation.Valid;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.NonNull;
import xp.librarian.model.context.ResourceNotFoundException;
import xp.librarian.model.dto.Book;
import xp.librarian.model.form.BookSearchForm;
import xp.librarian.model.form.PagingForm;
import xp.librarian.model.result.BookVM;
import xp.librarian.repository.BookDao;

/**
 * @author xp
 */
@Service("readerBookService")
@Transactional
public class BookService {

    @Autowired
    private Validator validator;

    @Autowired
    private BookDao bookDao;

    private BookVM buildBookVM(@NonNull Book book) {
        return new BookVM().withBook(book);
    }

    public List<BookVM> search(@Valid BookSearchForm form,
                               @Valid PagingForm paging) {
        form.validate(validator);

        Book where = new Book()
                .setIsbn(form.getIsbn())
                .setName(form.getName())
                .setStatus(Book.Status.NORMAL);
        List<Book> books = bookDao.search(where, paging.getOffset(), paging.getLimits());
        return books.stream()
                .filter(e -> e != null)
                .distinct()
                .map(this::buildBookVM)
                .collect(Collectors.toList());
    }

    public BookVM getBook(@NonNull String isbn) {
        Book book = bookDao.get(isbn);
        if (book == null) {
            throw new ResourceNotFoundException("book not found.");
        }
        return buildBookVM(book);
    }

}
