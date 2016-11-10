package xp.librarian.repository.impl;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.NonNull;
import xp.librarian.model.dto.Role;
import xp.librarian.model.dto.User;
import xp.librarian.model.dto.UserRole;
import xp.librarian.repository.UserDao;
import xp.librarian.repository.mapper.UserMapper;
import xp.librarian.repository.mapper.UserRoleMapper;
import xp.librarian.utils.TimeUtils;

/**
 * @author xp
 */
@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper roleMapper;

    private static final Function<Boolean, Predicate<User>> forceFilter =
            force -> e -> force || !User.Status.DELETED.equals(e.getStatus());

    public int add(@NonNull User user) {
        return userMapper.insert(user);
    }

    public int update(@NonNull User where,
                      @NonNull User set) {
        return userMapper.update(where, set);
    }

    public User get(@NonNull Long userId) {
        return get(userId, false);
    }

    public User get(@NonNull Long userId,
                    boolean force) {
        User where = new User();
        where.setId(userId);
        return get(where, force);
    }

    public User get(@NonNull User where) {
        return get(where, false);
    }

    public User get(@NonNull User where,
                    boolean force) {
        return gets(where, 0L, 1, force).stream()
                .findFirst().orElse(null);
    }

    public List<User> gets(@NonNull User where,
                           @NonNull Long offset,
                           @NonNull Integer limits) {
        return gets(where, offset, limits, false);
    }

    public List<User> gets(@NonNull User where,
                           @NonNull Long offset,
                           @NonNull Integer limits,
                           boolean force) {
        return userMapper.select(where, offset, limits).stream()
                .filter(forceFilter.apply(force))
                .map(e -> {
                    List<UserRole> roles = roleMapper.select(e.getId());
                    e.setRoles(roles.stream().map(UserRole::getRole).collect(Collectors.toSet()));
                    return e;
                })
                .collect(Collectors.toList());
    }

    public int addRole(@NonNull User user,
                       @NonNull Role role) {
        UserRole userRole = new UserRole()
                .setUserId(user.getId())
                .setRole(role)
                .setCreateTime(TimeUtils.now());
        int result = roleMapper.insert(userRole);
        if (result > 0) {
            user.getRoles().add(role);
        }
        return result;
    }

    public int removeRole(@NonNull User user,
                          @NonNull Role role) {
        UserRole where = new UserRole()
                .setUserId(user.getId())
                .setRole(role);
        int result = roleMapper.delete(where);
        if (result > 0) {
            user.getRoles().remove(role);
        }
        return result;
    }

}
