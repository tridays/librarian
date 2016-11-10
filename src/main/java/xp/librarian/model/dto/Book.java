package xp.librarian.model.dto;

import java.io.*;
import java.time.*;
import java.util.*;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author xp
 */
@Getter
@Setter
@Accessors(chain = true)
public class Book implements Serializable {

    private static final long serialVersionUID = -2528891458266093773L;

    private String isbn;

    private String name;

    public enum Status {
        NORMAL,
        DELETED,
        ;
    }

    private Status status;

    private String publisher;

    private String authors;

    private String imagePath;

    private String desc;

    private Instant createTime;

    private transient Instant updateTime;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Book)) return false;
        Book book = (Book) object;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
}
