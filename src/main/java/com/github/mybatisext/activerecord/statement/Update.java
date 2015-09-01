package com.github.mybatisext.activerecord.statement;

import com.github.mybatisext.annotation.Trans;


public interface Update<TABLE, ID> extends Statement<TABLE, ID> {

	// 根据主键更新，必须有ID值，只更新非null
	@Trans
	public int updateById( TABLE data );


	// 全部更新,包括null
	@Trans
	public int updateById( TABLE data, boolean isAll );


	// 根据给定的条件,按照给定的值进行更新
	@Trans
	public int update( TABLE condition, TABLE value );


	@Trans
	public int update( TABLE condition, String property, Object value );


	@Trans
	public int update( String conditionProperty, Object conditionValue, String property, Object value );


}
