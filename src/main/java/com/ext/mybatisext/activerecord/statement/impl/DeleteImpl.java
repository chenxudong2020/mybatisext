package com.ext.mybatisext.activerecord.statement.impl;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;

import com.ext.mybatisext.activerecord.Record;
import com.ext.mybatisext.activerecord.meta.TableMeta;
import com.ext.mybatisext.activerecord.sql.DeleteSQLBuilder;
import com.ext.mybatisext.activerecord.statement.Delete;

/**
 * <p>
 * 
 * @author 宋汝波
 * @date 2015年8月18日
 * @version 1.0.0
 */
public class DeleteImpl<TABLE, ID> extends BaseStatement<TABLE, ID> implements Delete<TABLE, ID> {

	MappedStatement deleteById;

	MappedStatement delete;


	public DeleteImpl( TableMeta<TABLE, ID> tm ) {

		super(tm);
		String sql = DeleteSQLBuilder.buildDeleteById(tableMeta);
		SqlSource sqlSource = new RawSqlSource(configuration, sql, tableMeta.getType());
		deleteById = getUpdateStatement(tableMeta.getName() + ".deleteById(ID)", sqlSource, SqlCommandType.DELETE);


		sql = DeleteSQLBuilder.buildDeleteWithScript(tableMeta);
		sqlSource = driver.createSqlSource(configuration, sql, tableMeta.getType());
		delete = getUpdateStatement(tableMeta.getName() + ".delete(TABLE)", sqlSource, SqlCommandType.DELETE);

	}


	@Override
	public int deleteById( ID id ) {

		return update(deleteById, id);

	}


	@Override
	public int delete( TABLE condition ) {
		return update(delete, condition);


	}


	@Override
	public int delete( String property, Object value ) {
		return delete(property, "=", value);

	}


	@Override
	public int delete( String property, String operator, Object value ) {

		Record condition = new Record();
		condition.put(property, value);

		String sql = DeleteSQLBuilder.buildDeleteByOne(tableMeta, property, operator);
		SqlSource sqlSource = driver.createSqlSource(configuration, sql, tableMeta.getType());
		MappedStatement deleteWhere = getUpdateStatement(tableMeta.getName() + ".delete( String, String, Object)",
			sqlSource, SqlCommandType.DELETE);
		return update(deleteWhere, condition);

	}


}
