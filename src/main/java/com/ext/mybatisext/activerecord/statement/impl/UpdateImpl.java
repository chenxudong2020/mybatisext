package com.ext.mybatisext.activerecord.statement.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;

import com.ext.mybatisext.activerecord.Record;
import com.ext.mybatisext.activerecord.meta.TableMeta;
import com.ext.mybatisext.activerecord.sql.UpdateSQLBuilder;
import com.ext.mybatisext.activerecord.statement.Update;

/**
 * <p>
 * 
 * @author 宋汝波
 * @date 2015年8月18日
 * @version 1.0.0
 */
public class UpdateImpl<TABLE, ID> extends BaseStatement<TABLE, ID> implements Update<TABLE, ID> {

	protected MappedStatement updateById;

	protected MappedStatement updateByIdAll;

	protected MappedStatement updateCondition;


	public UpdateImpl( TableMeta<TABLE, ID> tm ) {

		super(tm);
		String sql = UpdateSQLBuilder.buildUpdateById(tableMeta, false);
		sql = UpdateSQLBuilder.appendScript(sql);
		SqlSource sqlSource = driver.createSqlSource(configuration, sql, tm.getType());
		updateById = getUpdateStatement(tableMeta.getName() + ".updateById(TABLE)", sqlSource, SqlCommandType.UPDATE);

		sql = UpdateSQLBuilder.buildUpdateById(tableMeta, true);
		sql = UpdateSQLBuilder.appendScript(sql);
		sqlSource = driver.createSqlSource(configuration, sql, tm.getType());
		updateByIdAll = getUpdateStatement(tableMeta.getName() + ".updateById(TABLE,boolean)", sqlSource,
			SqlCommandType.UPDATE);


		sql = UpdateSQLBuilder.buildUpdate(tableMeta);
		sql = UpdateSQLBuilder.appendScript(sql);
		sqlSource = driver.createSqlSource(configuration, sql, tm.getType());
		updateCondition = getUpdateStatement(tableMeta.getName() + ".update(TABLE,TABLE)", sqlSource,
			SqlCommandType.UPDATE);


	}


	@Override
	public int updateById( TABLE table ) {
		Map<String, Object> map = toMap(table);
		if ( map.get(tableMeta.getIdName()) == null ) {
			throw new RuntimeException("您传入的数据没有主键");
		}
		return update(updateById, map);

	}


	@Override
	public int updateById( TABLE table, boolean isAll ) {
		Map<String, Object> map = toMap(table);
		if ( map.get(tableMeta.getIdName()) == null ) {
			throw new RuntimeException("您传入的数据没有主键");
		}
		if ( isAll ) {
			return update(updateByIdAll, map);
		}
		return update(updateById, map);

	}


	@Override
	public int update( TABLE condition, TABLE value ) {

		Map<String, Object> param = toMap(condition);

		Map<String, Object> sets = toMap(value);
		if ( sets.isEmpty() ) {
			throw new RuntimeException("更新字段不能为空");
		}
		Map<String, Object> where = new LinkedHashMap<String, Object>();
		for ( Map.Entry<String, Object> entry : param.entrySet() ) {
			where.put("where_" + entry.getKey(), entry.getValue());
		}
		// 合并  相同字段会替换
		where.putAll(sets);
		return update(updateCondition, where);

	}


	@Override
	public int update( TABLE condition, String property, Object value ) {

		Map<String, Object> param = toMap(condition);

		Map<String, Object> where = new LinkedHashMap<String, Object>();
		for ( Map.Entry<String, Object> entry : param.entrySet() ) {
			where.put("where_" + entry.getKey(), entry.getValue());
		}
		Record sets = new Record();
		sets.put(property, value);

		where.putAll(sets);
		return update(updateCondition, where);

	}


	@Override
	public int update( String conditionProperty, Object conditionValue, String property, Object value ) {

		Record where = new Record();
		where.put("where_" + conditionProperty, conditionValue);
		Record sets = new Record();
		sets.put(property, value);

		where.putAll(sets);


		return update(updateCondition, where);
	}


}
