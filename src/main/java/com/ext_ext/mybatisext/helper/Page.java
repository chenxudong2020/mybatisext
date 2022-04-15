package com.ext.mybatisext.helper;

import java.util.List;


public interface Page<T> {


	/**
	 * 当前是第几页,默认1,不能小于1
	 * <p>
	 *
	 * @return
	*/
	int getPageNo();


	/**
	 * 每页条数
	 * <p>
	 *
	 * @return
	*/
	int getPageSize();


	/**
	 * 分页结果记录
	 * <p>
	 *
	 * @param data
	*/
	void setRecords( List<T> data );


	/**
	 * 获取记录结果
	* <p>
	*
	* @return
	*/
	List<T> getRecords();


	/**
	 * 数据库中的总条数,自行计算总页数
	 * <p>
	 *
	 * @param count
	*/
	void setCount( int count );


	/**
	 * 获取总记录数
	 * <p>
	 *
	 * @return
	*/
	int getCount();


	/**
	 * 获取总页数
	 * <p>
	 *
	 * @return
	*/
	int getPages();
}
