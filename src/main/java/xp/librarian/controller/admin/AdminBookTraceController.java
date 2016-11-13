package xp.librarian.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xp.librarian.controller.BaseController;
import xp.librarian.model.form.BookTraceAddForm;
import xp.librarian.model.form.BookTraceUpdateForm;
import xp.librarian.model.form.PagingForm;
import xp.librarian.model.result.BookTraceVM;
import xp.librarian.service.admin.BookTraceService;

/**
 * @author xp
 */
@Api(
        description = "Admin::BookTraces 图书踪迹管理"
)
@RestController
@RequestMapping(value = "/admin/")
public class AdminBookTraceController extends BaseController {

    @Autowired
    private BookTraceService traceService;

    @ApiOperation(
            value = "添加图书踪迹",
            notes = "。",
            response = BookTraceVM.class
    )
    @PostMapping("books/{isbn:[0-9\\-]+}/traces/add")
    public Object addTrace(@PathVariable String isbn,
                           BookTraceAddForm form) {
        form.setIsbn(isbn);
        return renderForEntity(traceService.addTrace(form));
    }

    @ApiOperation(
            value = "更新图书踪迹",
            notes = "。",
            response = BookTraceVM.class
    )
    @PostMapping("books/{isbn:[0-9\\-]+}/traces/{traceId}/update")
    public Object updateTrace(@PathVariable String isbn,
                              @PathVariable Long traceId,
                              BookTraceUpdateForm form) {
        form.setIsbn(isbn);
        form.setTraceId(traceId);
        return renderForEntity(traceService.updateTrace(form));
    }

    @ApiOperation(
            value = "删除图书踪迹",
            notes = "其实只是更改状态为 DELETED。",
            response = BookTraceVM.class
    )
    @PostMapping("books/{isbn:[0-9\\-]+}/traces/{traceId}/delete")
    public Object deleteTrace(@PathVariable String isbn,
                              @PathVariable Long traceId) {
        traceService.deleteTrace(isbn, traceId);
        return renderForAction(true);
    }

    @ApiOperation(
            value = "查看图书踪迹列表",
            notes = "不同的是，管理员能看到 DELETED 状态的图书踪迹。",
            response = BookTraceVM.class,
            responseContainer = "List"
    )
    @GetMapping("books/{isbn:[0-9\\-]+}/traces/")
    public Object getTraces(@PathVariable String isbn,
                            PagingForm paging) {
        return renderForEntities(traceService.getTraces(isbn, paging));
    }

    @ApiOperation(
            value = "查看图书踪迹详情",
            notes = "目前跟列表里的没什么不同。",
            response = BookTraceVM.class
    )
    @GetMapping("books/{isbn:[0-9\\-]+}/traces/{traceId}/")
    public Object getTrace(@PathVariable String isbn,
                           @PathVariable Long traceId) {
        return renderForEntity(traceService.getTrace(isbn, traceId));
    }

}
