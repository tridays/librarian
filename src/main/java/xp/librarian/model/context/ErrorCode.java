package xp.librarian.model.context;

import lombok.Getter;

/**
 * @author xp
 */
public enum ErrorCode {
    ADMIN_BOOK_EXISTS("admin.book.exists"),

    ADMIN_USER_STATUS_MISMATCH("admin.user.status_mismatch"),
    ADMIN_BOOK_STATUS_MISMATCH("admin.book.status_mismatch"),
    ADMIN_BOOK_TRACE_STATUS_MISMATCH("admin.book_trace.status_mismatch"),
    ADMIN_LOAN_STATUS_MISMATCH("admin.loan.status_mismatch"),

    USER_EXISTS("user.exists"),
    USER_LOGIN_FAIL("user.login_fail"),

    BOOK_TRACE_STATUS_MISMATCH("book_trace.status_mismatch"),
    LOAN_STATUS_MISMATCH("loan.status_mismatch"),
    LOAN_REACH_MAX_RENEW_COUNT("loan.reach_max_renew_count"),
    LOAN_RESERVATION_EXISTS("loan.reservation_exists"),

    ;

    @Getter
    private String key;

    ErrorCode(String key) {
        this.key = key;
    }
}
