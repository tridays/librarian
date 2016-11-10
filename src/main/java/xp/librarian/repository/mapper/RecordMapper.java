package xp.librarian.repository.mapper;

import java.util.*;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import xp.librarian.model.dto.Record;

/**
 * @author xp
 */
@Mapper
public interface RecordMapper {

    int insert(Record record);

    List<Record> select(@Param("where") Record where,
                        @Param("offset") Long offset,
                        @Param("limits") Integer limits);

}
