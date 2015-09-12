package com.ext_ext.mybatisext.interceptor;

import java.lang.reflect.Method;

import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

import com.ext_ext.mybatisext.activerecord.DB;
import com.ext_ext.mybatisext.activerecord.MybatisExt;
import com.ext_ext.mybatisext.activerecord.Table;
import com.ext_ext.mybatisext.annotation.TableName;

/**
 * 用于支持共通的mapper增删改查操作
 * <p>
 * 
 * @author 宋汝波
 * @date 2015年2月4日
 * @version 1.0.0
 */
@SuppressWarnings("unchecked")
public class GenericMapperInterceptor implements MyBatisInterceptor {

	protected DB db;

	@Override
	@SuppressWarnings("rawtypes")
	public Object invoke(MyBatisInvocation handler) throws Throwable {
		Method method = handler.getMethod();
		Class<?> mapperClass = handler.getMapperInterface();
		if (db == null) {
			getDB(handler);
		}
		TableName tableName = mapperClass.getAnnotation(TableName.class);
		if (tableName != null) {
			if (tableName.type() == Void.class) {
				throw new RuntimeException("请在TableName注解中指定实体类型");
			}

			if (db != null) {
				// 继承Table接口
				if (Table.class == method.getDeclaringClass()) {
					Table table = db.active(tableName.name(), tableName.type(), tableName.id(), tableName.idType());
					return method.invoke(table, handler.getArgs());
				}
			}
		}
		// 页可以继承DB接口
		if (DB.class == method.getDeclaringClass()) {
			return method.invoke(db, handler.getArgs());
		}
		return handler.execute();

	}

	private synchronized void getDB(MyBatisInvocation handler) {
		if (db != null) {
			return;
		}
		DefaultSqlSessionFactory factory = new DefaultSqlSessionFactory(handler.getConfiguration());
		db = MybatisExt.open(factory);
	}

}
