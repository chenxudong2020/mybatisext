package com.github.mybatisext.activerecord.statement;

import java.util.List;

public interface Insert<TABLE, ID> extends Statement<TABLE, ID> {

	// 只插入非null的值,返回影响行数
	public int insert( TABLE data );


	// 处理返回的ID值,自动生成的ID,判定数据库类型
	public int insert( List<TABLE> data );


}
