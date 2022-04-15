package com.ext.mybatisext.activerecord.statement.impl;

import java.util.List;

import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;

import com.ext.mybatisext.activerecord.meta.TableMeta;
import com.ext.mybatisext.activerecord.sql.InsertSQLBuilder;
import com.ext.mybatisext.activerecord.statement.Insert;

/**
 * <p>
 * 
 * @author 宋汝波
 * @date 2015年8月17日
 * @version 1.0.0
 */
public class InsertImpl<TABLE, ID> extends BaseStatement<TABLE, ID> implements Insert<TABLE, ID> {

	private final MappedStatement insertListStatement;

	private final MappedStatement insertEntityStatement;


	public InsertImpl( TableMeta<TABLE, ID> tm ) {
		super(tm);
		String id_insert_entity = tableMeta.getName() + ".insert(TABLE)";
		String id_insert_list = tableMeta.getName() + ".insert(List)";

		// 预处理插入语句
		String insertListSQL = InsertSQLBuilder.buildInsertListSQL(tableMeta);
		SqlSource sqlSourceList = driver.createSqlSource(configuration, insertListSQL, tableMeta.getType());
		insertListStatement = getUpdateStatement(id_insert_list, sqlSourceList, SqlCommandType.INSERT);

		// 插入对象
		String insertEntitySQL = InsertSQLBuilder.buildInsertEntitySQL(tableMeta);
		SqlSource sqlSourceEntity = driver.createSqlSource(configuration, insertEntitySQL, tableMeta.getType());
		MappedStatement.Builder statement = new MappedStatement.Builder(configuration, id_insert_entity,
				sqlSourceEntity, SqlCommandType.INSERT);
		//支持主键自动生成
		statement.keyGenerator(new Jdbc3KeyGenerator());
		statement.keyProperty(tableMeta.getIdName());
		insertEntityStatement = statement.build();
	}


	@Override
	public int insert( TABLE data ) {
		return update(insertEntityStatement, data);
	}


	@Override
	public int insert( List<TABLE> data ) {
		return update(insertListStatement, data);

	}


}
