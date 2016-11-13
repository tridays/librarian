package xp.librarian.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xp.librarian.model.form.PagingForm;
import xp.librarian.model.result.BookTraceVM;
import xp.librarian.service.reader.BookTraceService;

/**
 * @author xp
 */
@Api(
        description = "BookTraces 图书踪迹"
)
@RestController
@RequestMapping(value = "/")
public class BookTraceController extends BaseController {

    @Autowired
    private BookTraceService traceService;

    @ApiOperation(
            value = "获取图书踪迹",
            notes = "具体到每一本实体书的记录。",
            response = BookTraceVM.class,
            responseContainer = "List"
    )
    @GetMapping("books/{isbn:[0-9\\-]+}/traces/")
    public Object getTraces(@PathVariable String isbn, PagingForm paging) {
        return renderForEntities(traceService.getTraces(isbn, paging));
    }

    @ApiOperation(
            value = "获取图书踪迹详情",
            notes = "目前跟列表里的没什么区别。",
            response = BookTraceVM.class
    )
    @GetMapping("books/{isbn:[0-9\\-]+}/traces/{traceId}/")
    public Object getTrace(@PathVariable String isbn, @PathVariable Long traceId) {
        return renderForEntity(traceService.getTrace(isbn, traceId));
    }

}
