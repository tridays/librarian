package xp.librarian.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import xp.librarian.model.form.LendBookForm;
import xp.librarian.model.form.LendListForm;
import xp.librarian.model.form.PagingForm;
import xp.librarian.model.form.ReserveBookForm;
import xp.librarian.model.result.LoanVM;
import xp.librarian.service.reader.LoanService;

/**
 * @author xp
 */
@Api(
        description = "Loen 租借"
)
@RestController
@RequestMapping(value = "/")
public class LoanController extends BaseController {

    @Autowired
    private LoanService loanService;

    @ApiOperation(
            value = "申请租借",
            notes = "只能对 NORMAL 状态的 BookTrace 使用，将使其转变为 LOCKED 状态。最后得到 APPLYING 状态的 Loan。",
            response = LoanVM.class
    )
    @PostMapping("books/{isbn:[0-9\\-]+}/traces/{traceId}/lend")
    public Object lendBook(@PathVariable String isbn, @PathVariable Long traceId, LendBookForm form) {
        form.setIsbn(isbn);
        form.setTraceId(traceId);
        return renderForEntity(loanService.lendBook(getAccount(), form));
    }

    @ApiOperation(
            value = "获取租借列表",
            notes = "",
            response = LoanVM.class,
            responseContainer = "List"
    )
    @GetMapping("users/self/loen/")
    public Object getLends(LendListForm form, PagingForm paging) {
        return renderForEntities(loanService.getLoen(getAccount(), form, paging));
    }

    @ApiOperation(
            value = "获取租借记录",
            notes = "目前跟列表里的没什么区别。",
            response = LoanVM.class
    )
    @GetMapping("users/self/loen/{loanId}/")
    public Object getLend(@PathVariable Long lendId) {
        return renderForEntity(loanService.getLoan(getAccount(), lendId));
    }

    @ApiOperation(
            value = "取消租借申请",
            notes = "只能对 APPLYING 状态的 Loan 使用，将转变为 CANCELED 状态。相应的 BookTrace 里 loanId 清零，但状态仍然为 LOCKED，它将由定时任务转变回 NORMAL 状态。"
    )
    @PostMapping("users/self/loen/{loanId}/cancel")
    public Object cancelLend(@PathVariable Long loanId) {
        return renderForEntity(loanService.cancelLending(getAccount(), loanId));
    }

    @ApiOperation(
            value = "续租",
            notes = "只能对 ACTIVE 状态的 Loan 使用，将 appointedTime 延后 30 天。"
    )
    @PostMapping("users/self/loen/{loanId}/renew")
    public Object renewLend(@PathVariable Long loanId) {
        return renderForEntity(loanService.renewLending(getAccount(), loanId));
    }

    @ApiOperation(
            value = "预约租借",
            notes = "只能对 BORROWED 状态的 BookTrace 使用。得到 RESERVING 状态的 Loan。",
            response = LoanVM.class
    )
    @PostMapping("books/{isbn:[0-9\\-]+}/traces/{traceId}/reserve")
    public Object reserveBook(@PathVariable String isbn, @PathVariable Long traceId, ReserveBookForm form) {
        form.setIsbn(isbn);
        form.setTraceId(traceId);
        return renderForEntity(loanService.reserveBook(getAccount(), form));
    }

    @ApiOperation(
            value = "取消预约",
            notes = "只能对 RESERVING 状态的 Loan 使用，将转变为 CANCELED 状态。",
            response = LoanVM.class
    )
    @PostMapping("users/self/loen/{loanId}/cancelReservation")
    public Object cancelReservation(@PathVariable Long loanId) {
        return renderForEntity(loanService.cancelReservation(getAccount(), loanId));
    }

}
