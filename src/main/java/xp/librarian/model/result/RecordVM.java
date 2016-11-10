package xp.librarian.model.result;

import java.io.*;
import java.time.*;
import java.util.*;

import lombok.Data;
import xp.librarian.model.dto.Action;
import xp.librarian.model.dto.Record;

/**
 * @author xp
 */
@Data
public class RecordVM implements Serializable {

    private static final long serialVersionUID = 4229075844008454538L;

    private Long id;

    private Long userId;

    private Long traceId;

    private Action action;

    private Long time;

    public RecordVM withRecord(Record record) {
        if (record != null) {
            this.id = record.getId();
            this.userId = record.getUserId();
            this.traceId = record.getTraceId();
            this.action = record.getAction();
            this.time = Optional.ofNullable(record.getTime()).map(Instant::toEpochMilli).orElse(null);
        }
        return this;
    }

}
