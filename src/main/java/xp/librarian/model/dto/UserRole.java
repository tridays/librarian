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
public class UserRole implements Serializable {

    private static final long serialVersionUID = -298349867748056048L;

    private Long userId;

    private Role role;

    private Instant createTime;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof UserRole)) return false;
        UserRole userRole = (UserRole) object;
        return Objects.equals(userId, userRole.userId) &&
                role == userRole.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, role);
    }
}
