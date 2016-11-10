package xp.librarian.repository.mapper;

import java.util.*;

import org.apache.ibatis.annotations.*;

/**
 * @author xp
 */
@Mapper
public interface GlobalVarMapper {

    @Select("SELECT `value` FROM `GlobalVar` WHERE `key`=#{key};")
    String select(@Param("key") String key);

    @Select("SELECT `key`, `value` FROM `GlobalVar`;")
    @MapKey("key")
    Map<String, Map<String, Object>> selectAll();

}
