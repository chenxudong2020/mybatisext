package com.ext_ext.mybatisext.activerecord.statement;

import com.ext_ext.mybatisext.annotation.Trans;


public interface Update<TABLE, ID> extends Statement<TABLE, ID> {

	/**
	 * 根据主键更新，必须有ID值，只更新非null
	 * <p>
	 *
	 * @param data
	 * @return
	*/
	@Trans("update")
	public int updateById( TABLE data );


	/**
	 * isAll=true 全部更新,包括null
	 * <p>
	 *
	 * @param data
	 * @param isAll
	 * @return
	*/
	@Trans("update")
	public int updateById( TABLE data, boolean isAll );


	/**
	 * 根据给定的条件,按照给定的值进行更新
	 * 条件用and连接
	 * <p>
	 *
	 * @param condition
	 * @param value
	 * @return
	*/
	@Trans("update")
	public int update( TABLE condition, TABLE value );


	/**
	 * 将符号条件的更新成一个值
	 * <p>
	 *
	 * @param condition 条件
	 * @param property 更新的字段
	 * @param value 值
	 * @return
	*/
	@Trans("update")
	public int update( TABLE condition, String property, Object value );


	/**
	 * 前两个是条件，后两个是结果
	 * <p>
	 *
	 * @param conditionProperty 查询字段
	 * @param conditionValue 查询值
	 * @param property 赋值字段
	 * @param value 值
	 * @return
	*/
	@Trans("update")
	public int update( String conditionProperty, Object conditionValue, String property, Object value );


}
