package xp.librarian.model.dto;

import java.io.*;
import java.util.*;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xp
 */
@Getter
@Setter
public class Lend implements Serializable {

    private static final long serialVersionUID = 7741202507373698255L;

    private Integer id;

    private Integer userId;

    private Integer traceId;

    public enum
    Status {
        APPLYING,   // 申请借书中
        CANCELED,   // 申请已过期
        EXPIRED,    // 申请已过期
        REJECTED,   // 申请被拒绝
        ACTIVE,     // 已借到
        LATE,       // 已违约
        RETURNED,   // 已归还
        DISABLED,   // 已失效（无法归还）
        ;
    }

    private Status status;

    private Date applyingTime;

    private Date appointedTime;

    private Date expiredTime;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Lend)) return false;
        Lend lend = (Lend) object;
        return Objects.equals(id, lend.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}