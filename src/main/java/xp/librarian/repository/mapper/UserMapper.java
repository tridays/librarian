package xp.librarian.repository.mapper;

import java.util.*;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import xp.librarian.model.dto.User;

/**
 * @author xp
 */
@Mapper
public interface UserMapper {

    int insert(User user);

    int update(@Param("where") User where,
               @Param("set") User set);

    List<User> select(@Param("where") User where,
                      @Param("offset") Long offset,
                      @Param("limits") Integer limits);

}
