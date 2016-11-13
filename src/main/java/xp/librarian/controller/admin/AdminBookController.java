package xp.librarian.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xp.librarian.controller.BaseController;
import xp.librarian.model.form.BookAddForm;
import xp.librarian.model.form.BookUpdateForm;
import xp.librarian.model.form.PagingForm;
import xp.librarian.model.result.BookVM;
import xp.librarian.service.admin.BookService;

/**
 * @author xp
 */
@Api(
        description = "Admin::Books 书目管理"
)
@Controller
@RequestMapping(value = "/admin/")
public class AdminBookController extends BaseController {

    @Autowired
    private BookService bookService;

    @ApiOperation(
            value = "添加书目",
            notes = "。",
            response = BookVM.class
    )
    @PostMapping("books/add")
    public Object addBook(BookAddForm form) {
        return renderForEntity(bookService.addBook(form));
    }

    @ApiOperation(
            value = "更新书目",
            notes = "。",
            response = BookVM.class
    )
    @PostMapping("books/{isbn:[0-9\\-]+}/update")
    public Object updateBook(@PathVariable String isbn,
                             BookUpdateForm form) {
        form.setIsbn(isbn);
        return renderForEntity(bookService.updateBook(form));
    }

    @ApiOperation(
            value = "删除书目",
            notes = "其实只是更改状态为 DELETED。"
    )
    @PostMapping("books/{isbn:[0-9\\-]+}/delete")
    public Object deleteBook(@PathVariable String isbn) {
        bookService.deleteBook(isbn);
        return renderForAction(true);
    }

    @ApiOperation(
            value = "查看书目列表",
            notes = "不同的是，管理员能看到 DELETED 状态的书目。",
            response = BookVM.class,
            responseContainer = "List"
    )
    @GetMapping("books/")
    public Object getBooks(PagingForm paging) {
        return renderForEntities(bookService.getBooks(paging));
    }

    @ApiOperation(
            value = "查看书目详情",
            notes = "目前跟列表里的没什么不同。",
            response = BookVM.class
    )
    @GetMapping("books/{isbn:[0-9\\-]+}/")
    public Object getBook(@PathVariable String isbn) {
        return renderForEntity(bookService.getBook(isbn));
    }

}
