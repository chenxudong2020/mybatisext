package com.ext_ext.mybatisext.activerecord;

import java.util.List;

import org.apache.ibatis.mapping.MappedStatement;

import com.ext_ext.mybatisext.activerecord.meta.DBMeta;
import com.ext_ext.mybatisext.annotation.Trans;
import com.ext_ext.mybatisext.helper.Page;

public interface DB {

	// 获取表的操作
	public <TABLE, ID> Table<TABLE, ID> active( String name, Class<TABLE> tableType, String idField, Class<ID> idType );


	// 带TableName注解的class
	public <TABLE, ID> Table<TABLE, ID> active( Class<TABLE> tableType );


	// 指定id字段名称
	public <ID> Table<Record, ID> active( String name, String idField, Class<ID> idType );


	// 默认id主键,Long类型
	public Table<Record, Long> active( String name );


	// 配置信息
	public DBMeta getDBMeta();


	/**
	 * 查询结果集
	 */
	// 自定义sql语句进行查询操作,返回结果集

	@Trans
	public List<Record> list( String sql, Object... parameter );


	@Trans
	public Page<Record> paging( Page<Record> page, String sql, Object... parameter );


	// 结果转换为提供的类

	@Trans
	public <T> List<T> list( String sql, Class<T> type, Object... parameter );


	@Trans
	public <T> Page<T> paging( Page<T> page, String sql, Class<T> type, Object... parameter );


	/**
	 * 只取第一条
	 * 
	 * @param sql
	 * @return
	 */

	@Trans
	public Record one( String sql, Object... parameter );


	@Trans
	public <T> T one( String sql, Class<T> type, Object... parameter );


	// 执行增删改操作，返回影响行数
	@Trans
	public int update( String sql, Object... parameter );


	// 数量统计
	@Trans
	public int count( String sql, Object... parameter );


	// 执行脚本
	@Trans
	public <T> List<T> queryScript( String script, Class<T> type, Object parameter );


	@Trans
	public int countScript( String script, Object parameter );


	@Trans
	public <T> Page<T> pagingScript( Page<T> page, String script, Class<T> type, Object parameter );


	@Trans
	public int updateScript( String script, Object parameter );


	// 语句
	@Trans
	public <TABLE> List<TABLE> query( MappedStatement statement, Object parameter );


	@Trans
	public int update( MappedStatement statement, Object parameter );
}
