package xp.librarian.model.form;

import java.io.*;
import java.util.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import xp.librarian.model.context.ValidationException;
import xp.librarian.model.dto.Book;

/**
 * @author xp
 */
@Data
public class BookAddForm implements Serializable {

    private static final long serialVersionUID = 4634452314301768988L;

    @NotNull
    @Length(min = 13, max = 13)
    @Pattern(regexp = "[0-9]+")
    private String isbn;

    @NotNull
    @Length(min = 1, max = 255)
    private String name;

    private Book.Status status;

    @Length(max = 50)
    private String publisher;

    @Length(max = 1023)
    private String authors;

    private MultipartFile image;

    @URL
    private String imageUrl;

    @Length(max = 65535)
    private String desc;

    public boolean validate(Validator validator) {
        Set<ConstraintViolation<BookAddForm>> vSet = validator.validate(this);
        if (!vSet.isEmpty()) {
            throw new ValidationException(vSet);
        }
        return true;
    }

    public Book forSet() {
        return new Book()
                .setIsbn(isbn)
                .setName(name)
                .setStatus(status)
                .setPublisher(publisher)
                .setAuthors(authors)
                .setDesc(desc);
    }

}
