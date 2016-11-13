package xp.librarian.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xp.librarian.model.form.BookSearchForm;
import xp.librarian.model.form.PagingForm;
import xp.librarian.model.result.BookVM;
import xp.librarian.service.reader.BookService;

/**
 * @author xp
 */
@Api(
        description = "Books 书目"
)
@RestController
@RequestMapping(value = "/")
public class BookController extends BaseController {

    @Autowired
    private BookService bookService;

    @ApiOperation(
            value = "搜索书目",
            notes = "得到的是逻辑上的书。方法就是普通的字符串匹配。",
            response = BookVM.class,
            responseContainer = "List"
    )
    @GetMapping("books/search")
    public Object search(BookSearchForm form, PagingForm paging) {
        return renderForEntities(bookService.search(form, paging));
    }

    @ApiOperation(
            value = "查看书目详情",
            notes = "其实跟列表里的没什么区别。",
            response = BookVM.class
    )
    @GetMapping("books/{isbn:[0-9\\-]+}/")
    public Object getBook(@PathVariable String isbn) {
        return renderForEntity(bookService.getBook(isbn));
    }

}
