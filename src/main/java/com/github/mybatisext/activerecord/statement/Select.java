package com.github.mybatisext.activerecord.statement;

import java.util.List;

import com.github.mybatisext.helper.Page;

public interface Select<TABLE, ID> extends Statement<TABLE, ID> {

	// 根据ID查询
	public TABLE selectById( ID id, String... columns );


	// 限定列
	public List<TABLE> list( TABLE condition, String... columns );


	// 限定列
	public List<TABLE> list( String field, Object value, String... columns );


	// 限定列
	public TABLE one( TABLE condition, String... columns );


	// 限定返回的列
	public TABLE one( String field, Object value, String... columns );


	// 总条数
	public int count();


	public int count( TABLE condition );


	public int count( String field, Object value );


	// 更多条数查询
	//  分页查询
	public Page<TABLE> paging( Page<TABLE> page, TABLE condition, String... columns );


	// 页码,从0开始,每页条数
	public List<TABLE> paging( int pageNo, int size, TABLE condition, String... columns );

}
