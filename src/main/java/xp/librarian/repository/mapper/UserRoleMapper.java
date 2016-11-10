package xp.librarian.repository.mapper;

import java.util.*;

import org.apache.ibatis.annotations.Param;

import xp.librarian.model.dto.UserRole;

/**
 * @author xp
 */
public interface UserRoleMapper {

    int insert(UserRole role);

    int delete(UserRole role);

    List<UserRole> select(@Param("userId") Long userId);

}
