package com.ext_ext.mybatisext.activerecord.statement;

import java.util.List;

import com.ext_ext.mybatisext.annotation.Trans;
import com.ext_ext.mybatisext.helper.Page;

public interface Select<TABLE, ID> extends Statement<TABLE, ID> {

	// 根据ID查询
	@Trans
	public TABLE selectById( ID id, String... columns );


	// 限定列
	@Trans
	public List<TABLE> list( TABLE condition, String... columns );


	// 限定列
	@Trans
	public List<TABLE> list( String field, Object value, String... columns );


	// 限定列
	@Trans
	public TABLE one( TABLE condition, String... columns );


	// 限定返回的列
	@Trans
	public TABLE one( String field, Object value, String... columns );


	// 总条数
	@Trans
	public int count();


	@Trans
	public int count( TABLE condition );


	@Trans
	public int count( String field, Object value );


	@Trans
	public Page<TABLE> paging( Page<TABLE> page, TABLE condition, String... columns );


	@Trans
	public List<TABLE> paging( int pageNo, int size, TABLE condition, String... columns );

}
