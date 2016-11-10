package xp.librarian.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import xp.librarian.model.context.*;
import xp.librarian.model.result.ResultWrapper;

/**
 * @author xp
 */
@RestControllerAdvice
public class ErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorHandler.class);

    // 200

    @ExceptionHandler({BusinessException.class})
    @ResponseStatus(HttpStatus.OK)
    public Object handleBusinessException(BusinessException e) {
        return ResultWrapper.error(e.getLocalizedMessage());
    }

    // 400

    @ExceptionHandler({BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleBindException(BindException e) {
        return ResultWrapper.error(e.getLocalizedMessage());
    }

    @ExceptionHandler({ServletRequestBindingException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleServletRequestBindingException(ServletRequestBindingException e) {
        return ResultWrapper.error(e.getLocalizedMessage());
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return ResultWrapper.error(e.getLocalizedMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResultWrapper.error(e.getLocalizedMessage());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return ResultWrapper.error(e.getLocalizedMessage());
    }

    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleValidationException(ValidationException e) {
        return ResultWrapper.error(e.getMessages());
    }

    // 401

    @ExceptionHandler({UnauthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Object handleUnauthorizedException(UnauthorizedException e) {
        return ResultWrapper.error(e.getLocalizedMessage());
    }

    // 403

    @ExceptionHandler({AccessForbiddenException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Object handleAccessForbiddenException(AccessForbiddenException e) {
        return ResultWrapper.error(e.getLocalizedMessage());
    }

    // 404

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleNoResourceFoundException(ResourceNotFoundException e) {
        return ResultWrapper.error(e.getLocalizedMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleNoHandlerFoundException(NoHandlerFoundException e) {
        return ResultWrapper.error(e.getLocalizedMessage());
    }

    // 405

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Object handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ResultWrapper.error(e.getLocalizedMessage());
    }

    // 500

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleRuntimeException(RuntimeException e) {
        LOG.info("RuntimeException", e);
        return ResultWrapper.error(e.getLocalizedMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleException(Exception e) {
        LOG.info("Exception", e);
        return ResultWrapper.error(e.getLocalizedMessage());
    }

}
