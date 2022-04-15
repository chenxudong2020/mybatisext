package com.ext.mybatisext.activerecord.proxy;

import org.apache.ibatis.transaction.Transaction;


/**
 * 放置connection对象
 * <p>

 * @author   宋汝波
 * @date	 2015年8月17日 
 * @version  1.0.0	 
 */
public abstract class TransactionHolder {

	private static ThreadLocal<Transaction> holder = new ThreadLocal<Transaction>();


	public static void set( Transaction value ) {
		Transaction trans = holder.get();
		if ( trans != null ) {
			throw new RuntimeException("多次分配数据库连接");
		}
		holder.set(value);
	}


	public static Transaction get() {
		Transaction trans = holder.get();
		/*if ( trans == null ) {
			throw new RuntimeException("没有分配数据库连接");
		}*/
		return trans;
	}


	public static void remove() {
		holder.remove();
	}
}
