package com.github.mybatisext.activerecord.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.ibatis.transaction.Transaction;

import com.github.mybatisext.activerecord.DB;
import com.github.mybatisext.activerecord.statement.Delete;
import com.github.mybatisext.activerecord.statement.Insert;
import com.github.mybatisext.activerecord.statement.Select;
import com.github.mybatisext.activerecord.statement.Update;
import com.github.mybatisext.annotation.Trans;

public class StatementProxy implements InvocationHandler {

	DB db;

	Object primary;


	StatementProxy( DB db, Object primary ) {
		this.db = db;
		this.primary = primary;
	}


	@Override
	public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {

		if ( method.getAnnotation(Trans.class) == null ) {
			return method.invoke(primary, args);
		}
		Transaction trans = db.getDBMeta().getTransaction();
		Object result = null;
		try {
			// 放置连接
			TransactionHolder.set(trans);
			// 调用接口
			result = method.invoke(primary, args);
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
	public static <T> T getStatementProxy( DB db, Object primary ) {
		StatementProxy proxy = new StatementProxy(db, primary);
		T statementProxy = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[ ] {
				Insert.class, Update.class, Select.class, Delete.class }, proxy);
		return statementProxy;
	}
}
