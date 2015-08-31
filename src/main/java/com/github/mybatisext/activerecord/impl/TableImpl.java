package com.github.mybatisext.activerecord.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import com.github.mybatisext.activerecord.DB;
import com.github.mybatisext.activerecord.Record;
import com.github.mybatisext.activerecord.Table;
import com.github.mybatisext.activerecord.config.ColumnsMapping;
import com.github.mybatisext.activerecord.meta.TableMeta;
import com.github.mybatisext.activerecord.proxy.StatementProxy;
import com.github.mybatisext.activerecord.sql.DeleteSQL;
import com.github.mybatisext.activerecord.sql.InsertSQL;
import com.github.mybatisext.activerecord.sql.SelectSQL;
import com.github.mybatisext.activerecord.sql.UpdateSQL;
import com.github.mybatisext.activerecord.sql.Where;
import com.github.mybatisext.activerecord.sql.Where.Clause;
import com.github.mybatisext.activerecord.statement.Delete;
import com.github.mybatisext.activerecord.statement.Insert;
import com.github.mybatisext.activerecord.statement.Select;
import com.github.mybatisext.activerecord.statement.Update;
import com.github.mybatisext.activerecord.statement.impl.DeleteImpl;
import com.github.mybatisext.activerecord.statement.impl.InsertImpl;
import com.github.mybatisext.activerecord.statement.impl.SelectImpl;
import com.github.mybatisext.activerecord.statement.impl.UpdateImpl;
import com.github.mybatisext.helper.Page;

public class TableImpl<TABLE, ID> implements Table<TABLE, ID> {

	protected static Log logger = LogFactory.getLog(TableImpl.class);

	protected TableMeta<TABLE, ID> tm = new TableMeta<TABLE, ID>();

	protected Insert<TABLE, ID> insert;

	protected Select<TABLE, ID> select;

	protected Update<TABLE, ID> update;

	protected Delete<TABLE, ID> delete;


	public TableImpl( DB db, String name, Class<TABLE> tableType, String idField, Class<ID> idType ) {

		tm.setDb(db);
		tm.setIdName(idField);
		tm.setIdType(idType);
		tm.setName(name);
		tm.setTable(this);
		tm.setType(tableType);

		// 映射关系
		ColumnsMapping mapping = db.getDBMeta().getColumnsMapping();
		tm.setParameterMappings(mapping.getParameterMapping(tm));
		tm.setResultMappings(mapping.getResultMapping(tm));
		tm.setPropertyColumnMapping(mapping.propertyColumnMapping(tm));
		Map<String, String> propertyMapping = tm.getPropertyColumnMapping();
		Map<String, String> columnMapping = new LinkedHashMap<String, String>();
		for ( Map.Entry<String, String> value : propertyMapping.entrySet() ) {
			columnMapping.put(value.getValue(), value.getKey());
		}
		tm.setColumnPropertyMapping(columnMapping);
		// 初始化语句接口
		insert = StatementProxy.getStatementProxy(tm.getDb(), new InsertImpl<TABLE, ID>(tm));
		select = StatementProxy.getStatementProxy(tm.getDb(), new SelectImpl<TABLE, ID>(tm));
		update = StatementProxy.getStatementProxy(tm.getDb(), new UpdateImpl<TABLE, ID>(tm));
		delete = StatementProxy.getStatementProxy(tm.getDb(), new DeleteImpl<TABLE, ID>(tm));

	}


	@Override
	public Select<TABLE, ID> getSelect() {

		return select;

	}


	@Override
	public Insert<TABLE, ID> getInsert() {

		return insert;

	}


	@Override
	public Delete<TABLE, ID> getDelete() {

		return delete;

	}


	@Override
	public Update<TABLE, ID> getUpdate() {

		return update;

	}


	@Override
	public TableMeta<TABLE, ID> getTableMeta() {
		return tm;
	}


	@Override
	public int excute( InsertSQL insertSql ) {

		String[] fields = insertSql.getFields();
		Object[] values = insertSql.getValues();

		StringBuilder sql = new StringBuilder(" INSERT INTO ");
		sql.append(tm.getName());
		Record rec = new Record();
		sql.append("(");
		for ( int i = 0 ; i < fields.length ; i++ ) {
			rec.put(fields[i], values[i]);
			if ( i != 0 ) {
				sql.append(" , ");
			}
			sql.append(fields[i]);
		}
		sql.append(") ");

		sql.append(" values (");
		for ( int i = 0 ; i < fields.length ; i++ ) {
			if ( i != 0 ) {
				sql.append(" , ");
			}
			sql.append("#{");
			sql.append(fields[i]);
			sql.append("}");
		}

		sql.append(")");
		return tm.getDb().updateScript(sql.toString(), rec);

	}


	@Override
	public List<TABLE> excute( SelectSQL selectSql ) {


		String[] fields = selectSql.getFields();
		List<Where> wheres = selectSql.getWhereClauses();
		String ordeBy = selectSql.getOrderBy();
		String groupBy = selectSql.getGroupBy();

		StringBuilder sql = new StringBuilder(" SELECT  ");

		for ( int i = 0 ; i < fields.length ; i++ ) {
			if ( i != 0 ) {
				sql.append(",");
			}
			sql.append(fields[i]);
		}
		sql.append(" FROM ");
		sql.append(tm.getName());
		Record parameter = new Record();
		for ( Where where : wheres ) {
			parameter.put(where.getField(), where.getValue());
			if ( where.getType() == Clause.WHERE ) {
				sql.append(" WHERE ");
			} else if ( where.getType() == Clause.AND ) {
				sql.append(" AND ");
			}
			sql.append(where.getField());
			sql.append(where.getOperator());
			sql.append("#{");
			sql.append(where.getField());
			sql.append("}");
		}

		if ( ordeBy != null ) {
			sql.append(" ORDER BY ");
			sql.append(ordeBy);
			sql.append(" ");
		}

		if ( groupBy != null ) {
			sql.append(" GROUP BY ");
			sql.append(groupBy);
			sql.append(" ");
		}

		return tm.getDb().queryScript(sql.toString(), tm.getType(), parameter);
	}


	@Override
	public int excute( UpdateSQL updateSql ) {

		Record parameter = new Record();
		Record sets = updateSql.getSets();
		StringBuilder sql = new StringBuilder(" UPDATE ");
		sql.append(tm.getName());
		sql.append(" <set> ");
		for ( Map.Entry<String, Object> entry : sets.entrySet() ) {
			sql.append(entry.getKey());
			sql.append("=");
			sql.append("#{");
			sql.append(entry.getKey());
			sql.append("},");

			parameter.put(entry.getKey(), entry.getValue());
		}
		sql.append(" </set> ");
		List<Where> wheres = updateSql.getWhereClauses();

		for ( Where where : wheres ) {
			parameter.put(where.getField(), where.getValue());
			if ( where.getType() == Clause.WHERE ) {
				sql.append(" WHERE ");
			} else if ( where.getType() == Clause.AND ) {
				sql.append(" AND ");
			}
			sql.append(where.getField());
			sql.append(where.getOperator());
			sql.append("#{");
			sql.append(where.getField());
			sql.append("}");
		}

		return tm.getDb().updateScript(sql.toString(), parameter);

	}


	@Override
	public int excute( DeleteSQL deleteSql ) {

		StringBuilder sql = new StringBuilder(" DELETE FROM ");
		sql.append(tm.getName());
		List<Where> wheres = deleteSql.getWhereClauses();
		Record parameter = new Record();
		for ( Where where : wheres ) {
			parameter.put(where.getField(), where.getValue());
			if ( where.getType() == Clause.WHERE ) {
				sql.append(" WHERE ");
			} else if ( where.getType() == Clause.AND ) {
				sql.append(" AND ");
			}
			sql.append(where.getField());
			sql.append(where.getOperator());
			sql.append("#{");
			sql.append(where.getField());
			sql.append("}");
		}

		return tm.getDb().updateScript(sql.toString(), parameter);

	}


	@Override
	public List<TABLE> list( String sql, Object... parameter ) {

		return tm.getDb().list(sql, tm.getType(), parameter);

	}


	@Override
	public Page<TABLE> paging( Page<TABLE> page, String sql, Object... parameter ) {


		return tm.getDb().paging(page, sql, tm.getType(), parameter);

	}


	@Override
	public TABLE one( String sql, Object... parameter ) {

		return tm.getDb().one(sql, tm.getType(), parameter);
	}


	@Override
	public int update( String sql, Object... parameter ) {


		return tm.getDb().update(sql, parameter);

	}


	@Override
	public List<TABLE> paging( int pageNo, int size, String sql, Object... parameter ) {
		String pagingSql = tm.getDb().getDBMeta().getDialectSQL().getPagingSQL((pageNo - 1) * size, size, sql);

		return tm.getDb().list(pagingSql, tm.getType(), parameter);

	}


	@Override
	public List<TABLE> queryScript( String script, Object parameter ) {

		return tm.getDb().queryScript(script, tm.getType(), parameter);

	}


	@Override
	public int updateScript( String script, Object parameter ) {


		return tm.getDb().updateScript(script, parameter);

	}


	@Override
	public Page<TABLE> pagingScript( Page<TABLE> page, String script, Object parameter ) {

		return tm.getDb().pagingScript(page, script, tm.getType(), parameter);

	}


	@Override
	public List<TABLE> pagingScript( int pageNo, int size, String script, Object parameter ) {

		String pagingSql = tm.getDb().getDBMeta().getDialectSQL().getPagingSQL((pageNo - 1) * size, size, script);


		return tm.getDb().queryScript(pagingSql, tm.getType(), parameter);
	}

}
