package com.github.mybatisext.activerecord.statement;


public interface Delete<TABLE, ID> extends Statement<TABLE, ID> {

	// 主键匹配
	public int deleteById( ID id );


	// 多条件匹配
	public int delete( TABLE condition );


	// 相等匹配
	public int delete( String property, Object value );


	// 操作符匹配字段
	public int delete( String property, String operator, Object value );


}
