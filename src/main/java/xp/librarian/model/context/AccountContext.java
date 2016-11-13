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

    private String email;

    private User.Status status;

    private Integer loanLimit;

    public User forWhere() {
        return new User()
                .setId(id);
    }

    public static AccountContext build(User user) {
        AccountContext account = new AccountContext();
        account.id = user.getId();
        account.roles = user.getRoles();
        account.email = user.getEmail();
        account.status = user.getStatus();
        account.loanLimit = user.getLoanLimit();
        return account;
    }

}
