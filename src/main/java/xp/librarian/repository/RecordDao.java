package xp.librarian.repository;

import java.util.*;

import xp.librarian.model.dto.Record;

/**
 * @author xp
 */
public interface RecordDao {

    int add(Record record);

    List<Record> select(Record where, Long offset, Integer limits);

}
