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
public class User implements Serializable {

    private static final long serialVersionUID = -3210175692337394666L;

    private Long id;

    private String username;

    private String password;

    private transient Set<Role> roles = new HashSet<>();

    public enum Status {
        NORMAL,
        FROZEN,
        DELETED,
        ;
    }

    private Status status;

    private String name;

    private String avatarPath;

    private Integer age;

    private String major;

    private String phone;

    private String email;

    private String remarks;

    private Instant createTime;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof User)) return false;
        User user = (User) object;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
