package xp.librarian.model.dto;

/**
 * @author xp
 */
public enum Action {
    // reader
    APPLY,                  // 申请借书
    CANCEL_APPLICATION,     // 取消借书申请
    RENEW,                  // 续借

    RESERVE,                // 预订
    CANCEL_RESERVATION,     // 取消预订

    // admin
    ACCEPT,                 // 接受借书
    REJECT,                 // 拒绝借书

    CONFIRM_RETURNED,       // 证实归还
    CONFIRM_DISABLED,       // 证实无法归还

    QUICK_LENDING,          // 快速借书
    QUICK_RETURN,           // 快速还书

    // system
    EXPIRED,                // 申请过期
    RESERVED,               // 已预约
    LATE,                   // 借书期限违约
    ;
}
