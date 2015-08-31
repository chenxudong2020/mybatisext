package com.github.mybatisext.plugin;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import com.github.mybatisext.helper.IdWorker;
import com.github.mybatisext.helper.Identity;

/**
 * 
 * 主键自动赋值实现
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class IdentityPlugin implements Interceptor {

	@SuppressWarnings("rawtypes")
	@Override
	public Object intercept( Invocation invocation ) throws Throwable {
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		if ( mappedStatement.getSqlCommandType() != SqlCommandType.INSERT ) {
			return invocation.proceed();
		}
		Object param = invocation.getArgs()[1];

		if ( param instanceof Identity ) {
			Identity identity = (Identity) param;
			if ( identity.getId() == null ) {
				identity.setId(IdWorker.getId());
			}
		} else if ( param instanceof Map ) {
			Map map = (Map) param;
			List list = (List) map.get("list");

			if ( list != null ) {
				for ( Object object : list ) {
					if ( object instanceof Identity ) {
						Identity identity = (Identity) object;
						if ( identity.getId() == null ) {
							identity.setId(IdWorker.getId());
						}
					}
				}
			}
		}
		return invocation.proceed();

	}


	@Override
	public Object plugin( Object target ) {

		return Plugin.wrap(target, this);

	}


	@Override
	public void setProperties( Properties properties ) {

	}

}