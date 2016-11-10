package xp.librarian.model.dto;

import java.io.*;
import java.time.*;
import java.util.*;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author xp
 */
@Getter
@Setter
@Accessors(chain = true)
public class Loan implements Serializable {

    private static final long serialVersionUID = 7741202507373698255L;

    private Long id;

    private Long userId;

    private Long traceId;

    public enum
    Status {
        APPLYING,   // 申请借书中
        RESERVING,  // 预订中
        CANCELED,   // 已取消
        EXPIRED,    // 申请已过期
        REJECTED,   // 申请被拒绝
        ACTIVE,     // 已借到
        RETURNED,   // 已归还
        DISABLED,   // 已失效（无法归还）
        ;
    }

    private Status status;

    private Boolean isReservation;

    private Boolean isLate;

    private Integer renewCount;

    private Integer appointedDuration;

    private Instant expiredTime;

    private Instant activeTime;

    private Instant appointedTime;

    private Instant createTime;

    private transient Instant updateTime;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Loan)) return false;
        Loan loan = (Loan) object;
        return Objects.equals(id, loan.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
