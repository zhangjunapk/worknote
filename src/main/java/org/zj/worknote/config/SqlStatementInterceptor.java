package org.zj.worknote.config;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.Properties;

/**
 * 拦截执行的sql
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})})
@Component
@Configuration
public class SqlStatementInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlStatementInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) {
        // 开始时间
        try {
            Object target = invocation.getTarget();

            Field delegate = target.getClass().getDeclaredField("delegate");
            delegate.setAccessible(true);
            Object delegateObj = delegate.get(target);
            System.out.println("fff");

            Field parameterHandler = delegateObj.getClass().getSuperclass().getDeclaredField("boundSql");
            parameterHandler.setAccessible(true);
            Object parameterHandlerObj = parameterHandler.get(delegateObj);

            Field parameterObject = parameterHandlerObj.getClass().getDeclaredField("parameterObject");
            parameterObject.setAccessible(true);
            Object o = parameterObject.get(parameterHandlerObj);
            System.out.println("拦截到执行的sql:--->" + o);

        } catch (Exception e) {
            e.printStackTrace();
        }
        long start = System.currentTimeMillis();
        invocation.getArgs();
        try {
            return invocation.proceed();
        } catch (Exception e) {
            LOGGER.error("执行失败！", e);
            return null;
        } finally {
            long end = System.currentTimeMillis();
            long time = end - start;
            LOGGER.info("cost time {}ms", time);
        }
    }

    @Override
    public Object plugin(Object arg0) {
        return Plugin.wrap(arg0, new SqlStatementInterceptor());
    }

    @Override
    public void setProperties(Properties arg0) {
    }
}