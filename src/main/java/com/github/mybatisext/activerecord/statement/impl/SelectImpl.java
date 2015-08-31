package com.github.mybatisext.activerecord.statement.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;

import com.github.mybatisext.activerecord.Record;
import com.github.mybatisext.activerecord.Table;
import com.github.mybatisext.activerecord.meta.TableMeta;
import com.github.mybatisext.activerecord.sql.SelectSQLBuilder;
import com.github.mybatisext.activerecord.statement.Select;
import com.github.mybatisext.helper.Page;

/**
 * 查询实现
 * <p>
 * 
 * @author 宋汝波
 * @date 2015年8月18日
 * @version 1.0.0
 */
public class SelectImpl<TABLE, ID> extends BaseStatement<TABLE, ID> implements Select<TABLE, ID> {

	protected MappedStatement selectList;

	protected MappedStatement selectById;

	protected MappedStatement count;

	protected String fields;


	public SelectImpl( TableMeta<TABLE, ID> tm ) {

		super(tm);
		String sql = SelectSQLBuilder.buildSelectListWithScript(tableMeta);
		SqlSource sqlSourceList = driver.createSqlSource(configuration, sql, Map.class);
		selectList = getQueryStatement(tableMeta.getName() + ".list(" + tableMeta.getType().getSimpleName() + ")",
			sqlSourceList);

		fields = SelectSQLBuilder.buildDynamicColumn(tableMeta);


		SqlSource sqlSourceSelectById = driver.createSqlSource(configuration, SelectSQLBuilder.buildSelectById(tm),
			tableMeta.getType());
		selectById = getQueryStatement(tableMeta.getName() + ".selectById", sqlSourceSelectById);


		//count语句
		sql = SelectSQLBuilder.buildSelectListWithScript(tableMeta);
		sqlSourceList = driver.createSqlSource(configuration, sql, Map.class);

		List<ResultMap> list = new ArrayList<ResultMap>(1);
		ResultMap.Builder resultMapBuilder = new ResultMap.Builder(configuration, tableMeta.getName() + "_countMap",
				Integer.class, new ArrayList<ResultMapping>(1));
		list.add(resultMapBuilder.build());
		MappedStatement.Builder statement = new MappedStatement.Builder(configuration, tableMeta.getName() + ".count",
				sqlSourceList, SqlCommandType.SELECT);
		statement.resultMaps(list);

		count = statement.build();
	}


	@Override
	public List<TABLE> list( TABLE table, String... columns ) {
		Map<String, Object> param = toMap(table);
		putDynamicColumn(param, columns);

		return query(selectList, param);
	}


	@Override
	public List<TABLE> list( String field, Object value, String... columns ) {

		Record param = new Record();
		param.put(field, value);
		putDynamicColumn(param, columns);

		return query(selectList, param);
	}


	protected void putDynamicColumn( Map<String, Object> param, String[] columns ) {
		if ( columns.length == 0 ) {
			param.put(SelectSQLBuilder.DYNAMIC_COLUMN, fields);
		} else {
			param.put(SelectSQLBuilder.DYNAMIC_COLUMN, SelectSQLBuilder.buildDynamicColumn(tableMeta, columns));
		}
	}


	@Override
	public Table<TABLE, ID> getTable() {

		return tableMeta.getTable();

	}


	@Override
	public TABLE selectById( ID id, String... columns ) {
		Record param = new Record();
		param.put(tableMeta.getIdName(), id);
		putDynamicColumn(param, columns);

		List<TABLE> list = query(selectById, param);
		if ( list.size() == 1 ) {
			return list.get(0);
		}
		if ( list.size() > 1 ) {
			throw new RuntimeException("根据主键查询的结果不能够大于1条");
		}
		return null;

	}


	@Override
	public TABLE one( TABLE condition, String... columns ) {
		List<TABLE> result = list(condition, columns);
		if ( result.size() > 0 ) {
			return result.get(0);
		}

		return null;
	}


	@Override
	public TABLE one( String field, Object value, String... columns ) {

		List<TABLE> result = list(field, value, columns);
		if ( result.size() > 0 ) {
			return result.get(0);
		}

		return null;

	}


	@Override
	public int count() {

		Record param = new Record();
		param.put(SelectSQLBuilder.DYNAMIC_COLUMN, " COUNT(*) as table_count ");

		return getCount(param, count);

	}


	private int getCount( Map<String, Object> param, MappedStatement statement ) {
		List<TABLE> list = query(statement, param);
		if ( list.size() > 0 ) {
			if ( list.get(0) == null ) {
				return 0;
			}
			return Integer.parseInt(list.get(0).toString());
		}
		return 0;
	}


	@Override
	public int count( TABLE condition ) {

		Map<String, Object> param = toMap(condition);
		param.put(SelectSQLBuilder.DYNAMIC_COLUMN, " COUNT(*) as table_count ");

		return getCount(param, count);

	}


	@Override
	public int count( String field, Object value ) {

		Record param = new Record();
		param.put(field, value);
		param.put(SelectSQLBuilder.DYNAMIC_COLUMN, " COUNT(*) as table_count ");


		return getCount(param, count);

	}


	@Override
	public Page<TABLE> paging( Page<TABLE> page, TABLE condition, String... columns ) {
		int pageNo = page.getPageNo();
		int size = page.getPageSize();
		Map<String, Object> param = toMap(condition);
		putDynamicColumn(param, columns);


		String sql = SelectSQLBuilder.buildSelectPaging(tableMeta, pageNo, size);
		SqlSource sqlSource = driver.createSqlSource(configuration, sql, Map.class);

		MappedStatement statement = getQueryStatement(tableMeta.getName() + ".paging(Page,TABLE,String...)", sqlSource);
		List<TABLE> data = query(statement, param);
		// 拼接count语句
		page.setCount(count(condition));
		page.setRecords(data);

		return page;


	}


	@Override
	public List<TABLE> paging( int pageNo, int size, TABLE condition, String... columns ) {
		String sql = SelectSQLBuilder.buildSelectPaging(tableMeta, pageNo, size);
		Map<String, Object> param = toMap(condition);
		putDynamicColumn(param, columns);

		SqlSource sqlSource = driver.createSqlSource(configuration, sql, Map.class);
		MappedStatement statement = getQueryStatement(tableMeta.getName() + ".paging(int,int)", sqlSource);

		return query(statement, param);

	}
}
