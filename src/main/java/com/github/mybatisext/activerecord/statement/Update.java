package com.github.mybatisext.activerecord.statement;


public interface Update<TABLE, ID> extends Statement<TABLE, ID> {

	// 根据主键更新，必须有ID值，只更新非null
	public int updateById( TABLE data );


	// 全部更新,包括null
	public int updateById( TABLE data, boolean isAll );


	// 根据给定的条件,按照给定的值进行更新
	public int update( TABLE condition, TABLE value );


	public int update( TABLE condition, String property, Object value );


	public int update( String conditionProperty, Object conditionValue, String property, Object value );


}
