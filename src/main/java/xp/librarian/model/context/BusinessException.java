package xp.librarian.model.context;

import java.util.*;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import xp.librarian.utils.I18N;

/**
 * @author xp
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 7872506909592221398L;

    @Getter
    private ErrorCode code;

    public BusinessException(ErrorCode code) {
        this(code, null);
    }

    public BusinessException(ErrorCode code, Throwable cause) {
        super(Optional.ofNullable(code).map(ErrorCode::getKey).orElse(StringUtils.EMPTY), cause);
        this.code = code;
    }

    @Override
    public String getLocalizedMessage() {
        return I18N.get(code.getKey());
    }

    @Override
    public Throwable fillInStackTrace() {
        return null;
    }
}
