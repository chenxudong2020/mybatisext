package com.github.mybatisext.interceptor;

import java.lang.reflect.Method;

import org.apache.ibatis.session.Configuration;

import com.github.mybatisext.annotation.TableName;
import com.github.mybatisext.mapper.MapperGenerate;


/**
 * 用于支持共通的mapper增删改查操作
 * <p>

 * @author   宋汝波
 * @date	 2015年2月4日 
 * @version  1.0.0	 
 */
public class AutoMapperInterceptor implements MyBatisInterceptor {

	@Override
	public Object invoke( MyBatisInvocation handler ) throws Throwable {
		Method method = handler.getMethod();
		Class<?> mapperClass = handler.getMapperInterface();
		TableName tableName = mapperClass.getAnnotation(TableName.class);
		if ( tableName != null ) {
			String methodName = method.getName();
			String className = mapperClass.getName();
			String statementName = className + "." + methodName;
			if ( !handler.getConfiguration().hasStatement(statementName, false) ) {
				addStatement(handler.getConfiguration(), statementName, mapperClass, tableName);
			}
		}
		return handler.execute();

	}


	/**
	 * 添加公共增删改查
	 * <p>
	 *
	 * @param configuration
	 * @param method
	 * @param mapperClass
	 * @param tableName 
	*/
	private synchronized void addStatement( Configuration configuration, String statementName, Class<?> mapperClass,
			TableName tableName ) {
		if ( configuration.hasStatement(statementName, false) ) {
			return;
		}
		MapperGenerate generate = new MapperGenerate(configuration, tableName.type(), mapperClass, tableName.name(),
				tableName.id());
		generate.build();

	}


}
