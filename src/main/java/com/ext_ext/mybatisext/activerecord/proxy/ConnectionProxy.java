package com.ext_ext.mybatisext.activerecord.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.ibatis.transaction.Transaction;

import com.ext_ext.mybatisext.activerecord.DB;

/**
 * 数据库操作代理
 * <p>
 * 
 * @author 宋汝波
 * @date 2015年8月17日
 * @version 1.0.0
 */
public class ConnectionProxy implements InvocationHandler {

	DB db;

	ConnectionProxy(DB realDB) {
		db = realDB;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String name = method.getName();
		if ("active".equals(name)) {
			return method.invoke(db, args);
		}

		Transaction trans = db.getDBMeta().getTransaction();
		if ("getTransaction".equals(name)) {
			return trans;
		}
		Object result = null;
		try {
			// 放置连接
			TransactionHolder.set(trans);
			// 调用接口
			result = method.invoke(db, args);
			// 提交
			trans.commit();
		} catch (Exception e) {
			// 回滚
			trans.rollback();
			if (e instanceof InvocationTargetException) {
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

	public static DB getDBProxy(DB realDB) {
		ConnectionProxy proxy = new ConnectionProxy(realDB);
		DB dbProxy = (DB) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class[] { DB.class }, proxy);
		return dbProxy;
	}

}
