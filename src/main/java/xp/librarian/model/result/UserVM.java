package xp.librarian.model.result;

import java.io.*;

import lombok.Data;
import xp.librarian.model.dto.User;
import xp.librarian.utils.UploadUtils;

/**
 * @author xp
 */
@Data
public class UserVM implements Serializable {

    private static final long serialVersionUID = 5154135571483181793L;

    private Long id;

    private String username;

    private String name;

    private String avatarUrl;

    public UserVM withUser(User user) {
        if (user != null) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.name = user.getName();
            this.avatarUrl = UploadUtils.makeUrl(user.getAvatarPath());
        }
        return this;
    }

}
