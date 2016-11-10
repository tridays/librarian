package xp.librarian.model.form;

import java.io.*;
import java.util.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import xp.librarian.model.context.ValidationException;
import xp.librarian.model.dto.Book;

/**
 * @author xp
 */
@Data
public class BookUpdateForm implements Serializable {

    private static final long serialVersionUID = 3424097931640604030L;

    @ApiModelProperty(hidden = true)
    @NotNull
    @Length(min = 10, max = 20)
    private String isbn;

    @Length(min = 1, max = 256)
    private String name;

    private Book.Status status;

    @Length(max = 50)
    private String publisher;

    @Length(max = 1023)
    private String authors;

    private MultipartFile image;

    @URL
    private String imageUrl;

    @Length(max = 63325)
    private String desc;

    public boolean validate(Validator validator) {
        Set<ConstraintViolation<BookUpdateForm>> vSet = validator.validate(this);
        if (!vSet.isEmpty()) {
            throw new ValidationException(vSet);
        }
        return true;
    }

    public Book forWhere() {
        return new Book()
                .setIsbn(isbn);
    }

    public Book forSet() {
        return new Book()
                .setName(name)
                .setStatus(status)
                .setPublisher(publisher)
                .setAuthors(authors)
                .setDesc(desc);
    }

}
