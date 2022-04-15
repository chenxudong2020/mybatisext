package com.ext.mybatisext.activerecord.statement;

import com.ext.mybatisext.annotation.Trans;


public interface Delete<TABLE, ID> extends Statement<TABLE, ID> {

	/**
	 * 主键匹配
	 * <p>
	 *
	 * @param id
	 * @return
	*/
	@Trans("update")
	public int deleteById( ID id );


	/**
	 * 多条件匹配,and连接多个属性
	 * <p>
	 *
	 * @param condition
	 * @return
	*/
	@Trans("update")
	public int delete( TABLE condition );


	/**
	 * 相等匹配
	 * <p>
	 *
	 * @param property
	 * @param value
	 * @return
	*/
	@Trans("update")
	public int delete( String property, Object value );


	/**
	 * 操作符匹配字段，传入操作符
	 * <p>
	 *
	 * @param property
	 * @param operator
	 * @param value
	 * @return
	*/
	@Trans("update")
	public int delete( String property, String operator, Object value );


}
