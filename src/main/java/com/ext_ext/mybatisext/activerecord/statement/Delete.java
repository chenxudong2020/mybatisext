package com.ext_ext.mybatisext.activerecord.statement;

import com.ext_ext.mybatisext.annotation.Trans;


public interface Delete<TABLE, ID> extends Statement<TABLE, ID> {

	// 主键匹配
	@Trans
	public int deleteById( ID id );


	// 多条件匹配
	@Trans
	public int delete( TABLE condition );


	// 相等匹配
	@Trans
	public int delete( String property, Object value );


	// 操作符匹配字段
	@Trans
	public int delete( String property, String operator, Object value );


}
