package xp.librarian.model.result;

import java.io.*;
import java.time.*;
import java.util.*;

import lombok.Data;
import xp.librarian.model.dto.Book;
import xp.librarian.utils.RegexUtils;
import xp.librarian.utils.UploadUtils;

/**
 * @author xp
 */
@Data
public class BookVM implements Serializable {

    private static final long serialVersionUID = 5131299075378859816L;

    private String isbn;

    private String name;

    private Book.Status status;

    private String publisher;

    private List<String> authors;

    private String imageUrl;

    private String desc;

    private Long total;

    private Long margin;

    private Long createTime;

    public BookVM withBook(Book book) {
        if (book != null) {
            this.isbn = book.getIsbn();
            this.name = book.getName();
            this.status = book.getStatus();
            this.publisher = book.getPublisher();
            this.authors = RegexUtils.extractAuthors(book.getAuthors());
            this.imageUrl = UploadUtils.makeUrl(book.getImagePath());
            this.desc = book.getDesc();
            this.createTime = Optional.ofNullable(book.getCreateTime()).map(Instant::toEpochMilli).orElse(null);
        }
        return this;
    }

    public BookVM withBookCount(Long total, Long margin) {
        this.total = total;
        this.margin = margin;
        return this;
    }

}
