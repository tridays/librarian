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
import xp.librarian.model.form.AdminLoanListForm;
import xp.librarian.model.form.AdminQuickLendForm;
import xp.librarian.model.form.PagingForm;
import xp.librarian.model.result.LoanVM;
import xp.librarian.service.admin.LoanService;

/**
 * @author xp
 */
@Api(
        description = "Admin::Loen 租借管理"
)
@Controller
@RequestMapping(value = "/admin/")
public class AdminLoanController extends BaseController {

    @Autowired
    private LoanService loanService;

    @ApiOperation(
            value = "查看租借列表",
            notes = "所有人的。",
            response = LoanVM.class,
            responseContainer = "List"
    )
    @GetMapping("loen/")
    public Object getLoen(AdminLoanListForm form, PagingForm paging) {
        return renderForEntities(loanService.getLoen(form, paging));
    }

    @ApiOperation(
            value = "查看租借详情",
            notes = "使用这个接口实现单本借书、还书功能。",
            response = LoanVM.class
    )
    @GetMapping("loen/{loanId}/")
    public Object getLoan(@PathVariable Long loanId) {
        return renderForEntity(loanService.getLoan(loanId));
    }

    @ApiOperation(
            value = "接受租借申请",
            notes = "将使 Loan 从 APPLYING 改变到 ACTIVE 状态，使 BookTrace 从 LOCKED 改变到 BORROWED 状态。"
    )
    @PostMapping("loen/{loanId}/accept")
    public Object acceptLending(@PathVariable Long loanId) {
        return renderForEntity(loanService.acceptLending(loanId));
    }

    @ApiOperation(
            value = "拒绝租借申请",
            notes = "将使 Loan 从 APPLYING 改变到 REJECTED 状态，使 BookTrace 的 loanId 清零，剩下的交给定时任务。"
    )
    @PostMapping("loen/{loanId}/reject")
    public Object rejectLending(@PathVariable Long loanId) {
        return renderForEntity(loanService.rejectLending(loanId));
    }

    @ApiOperation(
            value = "确认归还",
            notes = "将使 Loan 从 ACTIVE 改变到 RETURNED 状态，使 BookTrace 从 BORROWED 改变到 LOCKED 状态，剩下的交给定时任务。"
    )
    @PostMapping("loen/{loanId}/confirmReturned")
    public Object confirmReturned(@PathVariable Long loanId) {
        return renderForEntity(loanService.confirmReturned(loanId));
    }

    @ApiOperation(
            value = "确认无法归还",
            notes = "将使 Loan 从 ACTIVE 改变到 DISABLED 状态，使 BookTrace 从 BORROWED 改变到 DELETED 状态。"
    )
    @PostMapping("loen/{loanId}/confirmDisabled")
    public Object confirmDisabled(@PathVariable Long loanId) {
        return renderForEntity(loanService.confirmDisabled(loanId));
    }

    @PostMapping("books/{isbn:[0-9\\-]+}/traces/{traceId}/lending")
    public Object quickBookLending(@PathVariable String isbn, @PathVariable Long traceId, AdminQuickLendForm form) {
        form.setIsbn(isbn);
        form.setTraceId(traceId);
        return renderForEntity(loanService.quickLend(form));
    }

    @PostMapping("books/{isbn:[0-9\\-]+}/traces/{traceId}/returned")
    public Object quickConfirmReturned(@PathVariable String isbn, @PathVariable Long traceId) {
        return renderForEntity(loanService.quickReturn(isbn, traceId));
    }

}
