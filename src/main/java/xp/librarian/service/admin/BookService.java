package xp.librarian.service.admin;

import java.util.*;
import java.util.stream.*;

import javax.validation.Valid;
import javax.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.NonNull;
import xp.librarian.model.context.BusinessException;
import xp.librarian.model.context.ErrorCode;
import xp.librarian.model.context.ResourceNotFoundException;
import xp.librarian.model.dto.Book;
import xp.librarian.model.form.BookAddForm;
import xp.librarian.model.form.BookUpdateForm;
import xp.librarian.model.form.PagingForm;
import xp.librarian.model.result.BookVM;
import xp.librarian.repository.BookDao;
import xp.librarian.utils.TimeUtils;
import xp.librarian.utils.UploadUtils;

/**
 * @author xp
 */
@Service("adminBookService")
@Transactional
public class BookService {

    @Autowired
    private Validator validator;

    @Autowired
    private BookDao bookDao;

    private BookVM buildBookVM(@NonNull Book book) {
        return new BookVM().withBook(book);
    }

    public BookVM addBook(@Valid BookAddForm form) {
        form.validate(validator);

        Book existBook = bookDao.get(form.getIsbn(), true);
        if (existBook != null) {
            throw new BusinessException(ErrorCode.ADMIN_BOOK_EXISTS);
        }
        Book set = form.forSet();
        set.setImagePath(StringUtils.isEmpty(form.getImageUrl()) ?
                UploadUtils.upload(form.getImage()) : UploadUtils.fetch(form.getImageUrl()));
        if (set.getStatus() == null) {
            set.setStatus(Book.Status.NORMAL);
        }
        set.setCreateTime(TimeUtils.now());
        if (0 == bookDao.add(set)) {
            throw new PersistenceException("book insert failed.");
        }
        return buildBookVM(set);
    }

    public BookVM updateBook(@Valid BookUpdateForm form) {
        form.validate(validator);

        Book book = bookDao.get(form.getIsbn(), true);
        if (book == null) {
            throw new ResourceNotFoundException("book not found.");
        }
        Book where = new Book()
                .setIsbn(book.getIsbn())
                .setStatus(book.getStatus());
        Book set = form.forSet();
        set.setImagePath(StringUtils.isEmpty(form.getImageUrl()) ?
                UploadUtils.upload(form.getImage()) : UploadUtils.fetch(form.getImageUrl()));
        if (0 == bookDao.update(where, set)) {
            throw new PersistenceException("book update failed.");
        }
        return buildBookVM(bookDao.get(book.getIsbn(), true));
    }

    public void deleteBook(@NonNull String isbn) {
        Book book = bookDao.get(isbn, true);
        if (book == null) {
            throw new ResourceNotFoundException("book not found.");
        }
        if (!Book.Status.NORMAL.equals(book.getStatus())) {
            throw new BusinessException(ErrorCode.ADMIN_BOOK_STATUS_MISMATCH);
        }
        Book where = new Book()
                .setIsbn(isbn)
                .setStatus(Book.Status.NORMAL);
        Book set = new Book()
                .setStatus(Book.Status.DELETED);
        if (0 == bookDao.update(where, set)) {
            throw new PersistenceException("book update failed.");
        }
    }

    public List<BookVM> getBooks(@Valid PagingForm paging) {
        List<Book> books = bookDao.gets(new Book(), paging.getOffset(), paging.getLimits(), true);
        return books.stream()
                .filter((e) -> e != null)
                .distinct()
                .map(this::buildBookVM)
                .collect(Collectors.toList());
    }

    public BookVM getBook(@NonNull String isbn) {
        Book book = bookDao.get(isbn, true);
        if (book == null) {
            throw new ResourceNotFoundException("book not found.");
        }
        return buildBookVM(book);
    }

}
