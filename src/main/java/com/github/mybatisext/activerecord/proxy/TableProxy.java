package com.github.mybatisext.activerecord.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.ibatis.transaction.Transaction;

import com.github.mybatisext.activerecord.Table;


/**
 * 表对象操作代理
 * <p>

 * @author   宋汝波
 * @date	 2015年8月24日 
 * @version  1.0.0	 
 */
public class TableProxy<TABLE, ID> implements InvocationHandler {

	Table<TABLE, ID> table;


	public TableProxy( Table<TABLE, ID> table ) {
		this.table = table;
	}


	@Override
	public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {

		if ( method.getAnnotations().length == 0 ) {
			return method.invoke(table, args);
		}

		Transaction trans = table.getTableMeta().getDb().getDBMeta().getTransaction();

		Object result = null;
		try {
			// 放置连接
			TransactionHolder.set(trans);
			// 调用接口
			result = method.invoke(table, args);
			// 提交
			trans.commit();
		} catch ( Exception e ) {
			// 回滚
			trans.rollback();
			if ( e instanceof InvocationTargetException ) {
				throw ((InvocationTargetException) e).getCause();
			}
			throw e;
		} finally {
			// 移除
			TransactionHolder.remove();
			// 关闭连接
			trans.close();
		}

		return result;

	}


	@SuppressWarnings("unchecked")
	public static <TABLE, ID> Table<TABLE, ID> getTableProxy( Table<TABLE, ID> table ) {
		TableProxy<TABLE, ID> proxy = new TableProxy<TABLE, ID>(table);
		Table<TABLE, ID> tableProxy = (Table<TABLE, ID>) Proxy.newProxyInstance(Thread.currentThread()
				.getContextClassLoader(), new Class<?>[ ] { Table.class }, proxy);
		return tableProxy;
	}
}
