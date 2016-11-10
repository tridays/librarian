package xp.librarian.model.dto;

import java.io.*;
import java.time.*;
import java.util.*;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author xp
 */
@Getter
@Setter
@Accessors(chain = true)
public class Record implements Serializable {

    private static final long serialVersionUID = 8338137113945623443L;

    private Long id;

    private Long userId;

    private Long traceId;

    private Action action;

    private Object payload;

    private Instant time;

    public static Record apply(Loan loan) {
        Record record = new Record();
        record.setAction(Action.APPLY);
        record.setUserId(loan.getUserId());
        record.setTraceId(loan.getTraceId());
        record.setTime(Instant.now());
        return record;
    }

    public static Record cancelApplication(Loan loan) {
        Record record = new Record();
        record.setAction(Action.CANCEL_APPLICATION);
        record.setUserId(loan.getUserId());
        record.setTraceId(loan.getTraceId());
        record.setTime(Instant.now());
        return record;
    }

    public static Record renewLend(Loan loan) {
        Record record = new Record();
        record.setAction(Action.RENEW);
        record.setUserId(loan.getUserId());
        record.setTraceId(loan.getTraceId());
        record.setTime(Instant.now());
        return record;
    }

    public static Record reserve(Loan loan) {
        Record record = new Record();
        record.setAction(Action.RESERVE);
        record.setUserId(loan.getUserId());
        record.setTraceId(loan.getTraceId());
        record.setTime(Instant.now());
        return record;
    }

    public static Record cancelReservation(Loan loan) {
        Record record = new Record();
        record.setAction(Action.CANCEL_RESERVATION);
        record.setUserId(loan.getUserId());
        record.setTraceId(loan.getTraceId());
        record.setTime(Instant.now());
        return record;
    }

    public static Record accept(Loan loan) {
        Record record = new Record();
        record.setAction(Action.ACCEPT);
        record.setUserId(loan.getUserId());
        record.setTraceId(loan.getTraceId());
        record.setTime(Instant.now());
        return record;
    }

    public static Record reject(Loan loan) {
        Record record = new Record();
        record.setAction(Action.REJECT);
        record.setUserId(loan.getUserId());
        record.setTraceId(loan.getTraceId());
        record.setTime(Instant.now());
        return record;
    }

    public static Record confirmReturned(Loan loan) {
        Record record = new Record();
        record.setAction(Action.CONFIRM_RETURNED);
        record.setUserId(loan.getUserId());
        record.setTraceId(loan.getTraceId());
        record.setTime(Instant.now());
        return record;
    }

    public static Record confirmDisabled(Loan loan) {
        Record record = new Record();
        record.setAction(Action.CONFIRM_DISABLED);
        record.setUserId(loan.getUserId());
        record.setTraceId(loan.getTraceId());
        record.setTime(Instant.now());
        return record;
    }

    public static Record expired(Loan loan) {
        Record record = new Record();
        record.setAction(Action.EXPIRED);
        record.setUserId(loan.getUserId());
        record.setTraceId(loan.getTraceId());
        record.setTime(Instant.now());
        return record;
    }

    public static Record reserved(Loan loan) {
        Record record = new Record();
        record.setAction(Action.RESERVED);
        record.setUserId(loan.getUserId());
        record.setTraceId(loan.getTraceId());
        record.setTime(Instant.now());
        return record;
    }

    public static Record late(Loan loan) {
        Record record = new Record();
        record.setAction(Action.LATE);
        record.setUserId(loan.getUserId());
        record.setTraceId(loan.getTraceId());
        record.setTime(Instant.now());
        return record;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Record)) return false;
        Record record = (Record) object;
        return Objects.equals(id, record.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
