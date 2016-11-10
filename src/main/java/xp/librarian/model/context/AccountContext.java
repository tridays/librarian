package xp.librarian.model.context;

import java.io.*;
import java.util.*;

import lombok.Data;
import xp.librarian.model.dto.Role;
import xp.librarian.model.dto.User;

/**
 * @author xp
 */
@Data
public class AccountContext implements Serializable {

    private static final long serialVersionUID = 25302623067800609L;

    private Long id;

    private Set<Role> roles;

    private User.Status status;

    public User toDTO() {
        return new User()
                .setId(id);
    }

    public static AccountContext fromDTO(User user) {
        AccountContext account = new AccountContext();
        account.id = user.getId();
        account.roles = user.getRoles();
        account.status = user.getStatus();
        return account;
    }

}
