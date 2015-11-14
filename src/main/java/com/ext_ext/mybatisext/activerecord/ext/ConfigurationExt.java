package com.ext_ext.mybatisext.activerecord.ext;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * 扩展配置类
 * <p>
 * @author   bo
 * @Date	 2015-11-14 	 
 */
public class ConfigurationExt extends Configuration {

	public ConfigurationExt(Environment environment) {
		super(environment);
	}

	@Override
	public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement,
			RowBounds rowBounds, ParameterHandler parameterHandler, ResultHandler resultHandler, BoundSql boundSql) {
		ResultSetHandler resultSetHandler = new DefaultResultSetHandlerExt(executor, mappedStatement, parameterHandler,
				resultHandler, boundSql, rowBounds);
		resultSetHandler = (ResultSetHandler) interceptorChain.pluginAll(resultSetHandler);
		return resultSetHandler;
	}
}
