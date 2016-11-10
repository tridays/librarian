package xp.librarian.config.mybatis.interceptor;

import java.sql.*;
import java.util.*;

import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;

/**
 * @author xp
 */
@Intercepts(
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
)
@Component
public class ResultSetHandlerInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
//        Object target = invocation.getTarget();
//        if (target instanceof DefaultResultSetHandler) {
//            DefaultResultSetHandler resultSetHandler = (DefaultResultSetHandler) target;
//            Statement statement = (Statement) invocation.getArgs()[0];
//        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
