package xp.librarian.model.result;

import java.io.*;
import java.time.*;
import java.util.*;

import lombok.Data;
import xp.librarian.model.dto.Role;
import xp.librarian.model.dto.User;
import xp.librarian.utils.UploadUtils;

/**
 * @author xp
 */
@Data
public class UserProfileVM implements Serializable {

    private static final long serialVersionUID = -4849194255600852709L;

    private Long id;

    private String username;

    private Role[] roles;

    private String name;

    private String avatarUrl;

    private Integer age;

    private String major;

    private String phone;

    private String email;

    private String remarks;

    private Integer loanLimit;

    private Long createTime;

    public UserProfileVM withUser(User user) {
        if (user != null) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.roles = Optional.ofNullable(user.getRoles()).map(e -> e.toArray(new Role[0])).orElse(null);
            this.name = user.getName();
            this.avatarUrl = UploadUtils.makeUrl(user.getAvatarPath());
            this.age = user.getAge();
            this.major = user.getMajor();
            this.phone = user.getPhone();
            this.email = user.getEmail();
            this.remarks = user.getRemarks();
            this.loanLimit = user.getLoanLimit();
            this.createTime = Optional.ofNullable(user.getCreateTime()).map(Instant::toEpochMilli).orElse(null);
        }
        return this;
    }

}
