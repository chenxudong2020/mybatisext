package com.ext_ext.mybatisext.activerecord.statement;

import java.util.List;

import com.ext_ext.mybatisext.annotation.Trans;

public interface Insert<TABLE, ID> extends Statement<TABLE, ID> {

	/**
	 * 只插入非null的值,返回影响行数,支持自增主键返回
	 * <p>
	 *
	 * @param data
	 * @return
	*/
	@Trans("update")
	public int insert( TABLE data );


	/**
	 * 一次插入多条，不支持自增主键返回，同insert into XX values(?),(?)
	 * <p>
	 *
	 * @param data
	 * @return
	*/
	@Trans("update")
	public int insert( List<TABLE> data );


}
