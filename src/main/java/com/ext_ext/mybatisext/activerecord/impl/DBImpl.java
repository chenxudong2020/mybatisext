package com.ext_ext.mybatisext.activerecord.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import com.ext_ext.mybatisext.activerecord.DB;
import com.ext_ext.mybatisext.activerecord.Record;
import com.ext_ext.mybatisext.activerecord.Table;
import com.ext_ext.mybatisext.activerecord.meta.DBMeta;
import com.ext_ext.mybatisext.activerecord.proxy.DBProxy;
import com.ext_ext.mybatisext.activerecord.proxy.TransactionHolder;
import com.ext_ext.mybatisext.annotation.TableName;
import com.ext_ext.mybatisext.helper.CloseHelper;
import com.ext_ext.mybatisext.helper.Page;
import com.ext_ext.mybatisext.helper.PropertyHelper;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DBImpl implements DB {

	protected static Log logger = LogFactory.getLog(DBImpl.class);

	protected ConcurrentHashMap<String, Table<?, ?>> tableCache = new ConcurrentHashMap<String, Table<?, ?>>();

	protected DBMeta dbMeta;


	protected DB dbProxy;


	public DBImpl( SqlSessionFactory factory ) {
		dbMeta = new DBMeta(factory);
		dbProxy = DBProxy.getDBProxy(this);
	}


	public DB getDBProxy() {
		return dbProxy;
	}


	private List<Record> getList( ResultSet set ) throws SQLException {
		List<Record> result = new ArrayList<Record>();
		ResultSetMetaData meta = set.getMetaData();
		while ( set.next() ) {
			Record rec = new Record();
			for ( int i = 1 , size = meta.getColumnCount() ; i <= size ; i++ ) {
				String label = meta.getColumnLabel(i);
				rec.put(label, set.getObject(i));
			}
			result.add(rec);
		}

		return result;
	}


	private Record getOne( ResultSet set ) throws SQLException {
		ResultSetMetaData meta = set.getMetaData();
		Record rec = new Record();
		if ( set.next() ) {
			for ( int i = 1 , size = meta.getColumnCount() ; i <= size ; i++ ) {
				String label = meta.getColumnLabel(i);
				rec.put(label, set.getObject(i));
			}
		}
		return rec;
	}


	@Override
	public List<Record> list( String sql, Object... parameter ) {
		PreparedStatement prepare = null;
		ResultSet set = null;
		try {
			Connection conn = TransactionHolder.get().getConnection();
			prepare = conn.prepareStatement(sql);
			if ( parameter.length > 0 ) {
				setPs(prepare, parameter);
			}
			set = prepare.executeQuery();
			return getList(set);
		} catch ( SQLException e ) {
			logger.error("", e);
			throw new RuntimeException(e);
		} finally {
			CloseHelper.close(null, prepare, set);
		}
	}


	private <T> List<T> copyList( List<Record> recordList, Class<T> type ) {
		List<T> objList = new ArrayList<T>();
		ObjectFactory factory = dbMeta.getConfiguration().getObjectFactory();
		for ( Record record : recordList ) {
			T newObj = factory.create(type);
			MetaObject metaObj = dbMeta.getConfiguration().newMetaObject(newObj);

			for ( Map.Entry<String, Object> t : record.entrySet() ) {
				metaObj.setValue(t.getKey(), t.getValue());
			}

			objList.add((T) metaObj.getOriginalObject());

		}
		return objList;
	}


	@Override
	public <T> List<T> list( String sql, Class<T> type, Object... parameter ) {
		List<Record> recordList = list(sql, parameter);

		return copyList(recordList, type);
	}


	@Override
	public Record one( String sql, Object... parameter ) {
		PreparedStatement prepare = null;
		ResultSet set = null;
		try {
			Connection conn = TransactionHolder.get().getConnection();
			prepare = conn.prepareStatement(sql);
			if ( parameter.length > 0 ) {
				setPs(prepare, parameter);
			}
			set = prepare.executeQuery();
			return getOne(set);
		} catch ( SQLException e ) {
			logger.error("", e);
			throw new RuntimeException(e);
		} finally {
			CloseHelper.close(null, prepare, set);
		}
	}


	@Override
	public <T> T one( String sql, Class<T> type, Object... parameter ) {
		Record rec = one(sql, parameter);
		T newObj = dbMeta.getConfiguration().getObjectFactory().create(type);
		PropertyHelper.map2Object(rec, newObj);

		return newObj;
	}


	private void setPs( PreparedStatement prepared, Object... parameter ) throws SQLException {
		TypeHandlerRegistry typeRegistry = dbMeta.getConfiguration().getTypeHandlerRegistry();
		for ( int i = 0 ; i < parameter.length ; i++ ) {
			Object value = parameter[i];
			if ( value == null ) {
				TypeHandler type = typeRegistry.getTypeHandler(Object.class);
				type.setParameter(prepared, i + 1, null, dbMeta.getConfiguration().getJdbcTypeForNull());
			} else {
				TypeHandler type = typeRegistry.getTypeHandler(value.getClass());
				type.setParameter(prepared, i + 1, value, null);
			}
		}
	}


	@Override
	public int update( String sql, Object... parameter ) {
		int count = 0;
		PreparedStatement prepare = null;
		try {
			Connection conn = TransactionHolder.get().getConnection();
			prepare = conn.prepareStatement(sql);
			setPs(prepare, parameter);
			count = prepare.executeUpdate();
			//			ResultSet result = prepare.getGeneratedKeys();
			//			if ( result.next() ) {
			//				result.getLong(1);
			//			}
		} catch ( SQLException e ) {
			logger.error("", e);
			throw new RuntimeException(e);
		} finally {
			CloseHelper.close(null, prepare, null);
		}
		return count;
	}


	@Override
	public <TABLE, ID> Table<TABLE, ID> active( String name, Class<TABLE> tableType, String idField, Class<ID> idType ) {

		StringBuilder key = new StringBuilder(name);
		key.append(tableType.getName());
		key.append(idField);
		key.append(idType.getName());

		Table<TABLE, ID> table = (Table<TABLE, ID>) tableCache.get(key.toString());
		if ( table != null ) {
			return table;
		}
		table = new TableImpl<TABLE, ID>(dbProxy, name, tableType, idField, idType).getTableProxy();

		// 加入缓存
		tableCache.put(key.toString(), table);
		return table;
	}


	@Override
	public <ID> Table<Record, ID> active( String name, String idField, Class<ID> idType ) {

		return active(name, Record.class, idField, idType);
	}


	@Override
	public <TABLE, ID> Table<TABLE, ID> active( Class<TABLE> tableType ) {

		// 检测有没有注解
		TableName tableName = tableType.getAnnotation(TableName.class);
		if ( tableName == null ) {
			throw new RuntimeException("实体类没有TableName注解");
		}
		if ( tableName.type() == Void.class ) {
			return active(tableName.name(), tableType, tableName.id(), tableName.idType());
		}
		return active(tableName.name(), tableName.type(), tableName.id(), tableName.idType());

	}


	@Override
	public int count( String sql, Object... parameter ) {
		PreparedStatement prepare = null;
		ResultSet set = null;
		try {
			Connection conn = TransactionHolder.get().getConnection();
			prepare = conn.prepareStatement(sql);
			if ( parameter.length > 0 ) {
				setPs(prepare, parameter);
			}
			set = prepare.executeQuery();
			return getCount(set);
		} catch ( SQLException e ) {
			logger.error("", e);
			throw new RuntimeException(e);
		} finally {
			CloseHelper.close(null, prepare, set);
		}

	}


	private int getCount( ResultSet set ) throws SQLException {
		int count = 0;
		if ( set.next() ) {
			Object obj = set.getObject(1);
			if ( obj != null ) {
				count = Integer.parseInt(obj.toString());
			}
		}
		return count;
	}


	@Override
	public <TABLE> List<TABLE> query( MappedStatement statement, Object parameter ) {
		try {
			Executor executor = dbMeta.getConfiguration().newExecutor(TransactionHolder.get());
			List<TABLE> list = executor.query(statement, dbMeta.wrapCollection(parameter), RowBounds.DEFAULT, null,
				null, null);
			return list;
		} catch ( SQLException e ) {
			logger.error("", e);
			throw new RuntimeException(e);
		}
	}


	@Override
	public int update( MappedStatement statement, Object parameter ) {
		try {
			Executor executor = dbMeta.getConfiguration().newExecutor(TransactionHolder.get());
			return executor.update(statement, dbMeta.wrapCollection(parameter));
		} catch ( SQLException e ) {
			logger.error("", e);
			throw new RuntimeException(e);
		}

	}


	@Override
	public Table<Record, Long> active( String name ) {

		return active(name, "id", Long.class);

	}


	@Override
	public <T> List<T> queryScript( String script, Class<T> type, Object parameter ) {
		StringBuilder sql = new StringBuilder("<script>");
		sql.append(script);
		sql.append("</script>");
		SqlSource sqlSource = dbMeta.getXMLDriver().createSqlSource(dbMeta.getConfiguration(), sql.toString(),
			parameter.getClass());
		MappedStatement.Builder statement = new MappedStatement.Builder(dbMeta.getConfiguration(),
				"DB.queryScript(String,Class,Object)", sqlSource, SqlCommandType.SELECT);

		List<ResultMap> resultMaps = new ArrayList<ResultMap>(1);
		ResultMap.Builder builder = new ResultMap.Builder(dbMeta.getConfiguration(), "DB_ResultMap", type,
				new ArrayList<ResultMapping>(1));
		resultMaps.add(builder.build());
		statement.resultMaps(resultMaps);

		MappedStatement select = statement.build();

		return query(select, parameter);

	}


	@Override
	public int updateScript( String script, Object parameter ) {

		StringBuilder sql = new StringBuilder("<script>");
		sql.append(script);
		sql.append("</script>");
		SqlSource sqlSource = dbMeta.getXMLDriver().createSqlSource(dbMeta.getConfiguration(), sql.toString(),
			parameter.getClass());
		MappedStatement.Builder statement = new MappedStatement.Builder(dbMeta.getConfiguration(),
				"DB.updateScript(String,Object)", sqlSource, SqlCommandType.UNKNOWN);

		MappedStatement update = statement.build();

		return update(update, parameter);

	}


	@Override
	public Page<Record> paging( Page<Record> page, String sql, Object... parameter ) {

		return paging(page, sql, Record.class, parameter);
	}


	@Override
	public <T> Page<T> paging( Page<T> page, String sql, Class<T> type, Object... parameter ) {

		String pagingSql = dbMeta.getDialectSQL().getPagingSQL((page.getPageNo() - 1) * page.getPageSize(),
			page.getPageSize(), sql);
		String countSql = dbMeta.getDialectSQL().getPagingCountSQL(sql);

		int count = count(countSql, parameter);
		List<T> records = list(pagingSql, type, parameter);

		page.setCount(count);
		page.setRecords(records);

		return page;

	}


	@Override
	public <T> Page<T> pagingScript( Page<T> page, String script, Class<T> type, Object parameter ) {
		String pagingSql = dbMeta.getDialectSQL().getPagingSQL((page.getPageNo() - 1) * page.getPageSize(),
			page.getPageSize(), script);

		String countSql = dbMeta.getDialectSQL().getPagingCountSQL(script);
		List<T> records = queryScript(pagingSql, type, parameter);
		int count = countScript(countSql, parameter);

		page.setCount(count);
		page.setRecords(records);

		return page;

	}


	@Override
	public DBMeta getDBMeta() {
		return this.dbMeta;
	}


	@Override
	public int countScript( String script, Object parameter ) {

		StringBuilder sql = new StringBuilder("<script>");
		sql.append(script);
		sql.append("</script>");
		SqlSource sqlSource = dbMeta.getXMLDriver().createSqlSource(dbMeta.getConfiguration(), sql.toString(),
			parameter.getClass());
		MappedStatement.Builder statement = new MappedStatement.Builder(dbMeta.getConfiguration(),
				"DB.count(String,Object)", sqlSource, SqlCommandType.SELECT);

		List<ResultMap> resultMaps = new ArrayList<ResultMap>(1);
		ResultMap.Builder builder = new ResultMap.Builder(dbMeta.getConfiguration(), "DB_ResultMap", Integer.class,
				new ArrayList<ResultMapping>(1));
		resultMaps.add(builder.build());
		statement.resultMaps(resultMaps);

		MappedStatement select = statement.build();
		return count(select, parameter);

	}


	private int count( MappedStatement statement, Object parameter ) {

		if ( statement.getSqlCommandType() != SqlCommandType.SELECT ) {
			throw new RuntimeException("不是查询语句");
		}
		try {
			Executor executor = dbMeta.getConfiguration().newExecutor(TransactionHolder.get());
			List list = executor.query(statement, parameter, RowBounds.DEFAULT, null, null, null);
			if ( list.size() == 1 ) {
				return Integer.parseInt(list.get(0).toString());
			}
			if ( list.size() > 1 ) {
				throw new RuntimeException("count语句结果不能大于1");
			}
			return 0;
		} catch ( SQLException e ) {
			logger.error("", e);
			throw new RuntimeException(e);
		}

	}

}
