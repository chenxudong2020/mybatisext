package com.ext.mybatisext.activerecord.statement.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import com.ext.mybatisext.activerecord.DB;
import com.ext.mybatisext.activerecord.Table;
import com.ext.mybatisext.activerecord.meta.TableMeta;
import com.ext.mybatisext.activerecord.statement.Statement;
import com.ext.mybatisext.helper.PropertyHelper;

@SuppressWarnings("unchecked")
public abstract class BaseStatement<TABLE, ID> implements Statement<TABLE, ID> {

	protected Log logger = LogFactory.getLog(this.getClass());

	/**
	 * 数据操作对象
	 */
	protected DB db;

	/**
	 * session工厂
	 */
	protected SqlSessionFactory factory;

	/**
	 * mybatis配置
	 */
	protected Configuration configuration;

	/**
	 * 表数据
	 */
	protected TableMeta<TABLE, ID> tableMeta;

	/**
	 * 解析脚本驱动
	 */
	protected LanguageDriver driver;


	public BaseStatement( TableMeta<TABLE, ID> tm ) {
		this.db = tm.getDb();
		this.tableMeta = tm;
		this.factory = db.getDBMeta().getSessionFactory();
		this.configuration = db.getDBMeta().getConfiguration();
		this.driver = db.getDBMeta().getXMLDriver();


	}


	/**
	 * 将对象转成map
	 * <p>
	 *
	 * @param data
	 * @return
	 */
	protected Map<String, Object> toMap( TABLE data ) {
		Map<String, Object> value = null;
		if ( data == null ) {
			return new LinkedHashMap<String, Object>();
		}
		if ( data instanceof Map ) {
			value = (Map<String, Object>) data;
		} else {
			value = PropertyHelper.getPropertiesValue(data);
		}

		return value;
	}


	/**
	 * 执行查询此操作
	 * <p>
	 *
	 * @param statement
	 * @param parameter
	 * @return
	 */
	protected List<TABLE> query( MappedStatement statement, Object parameter ) {
		return db.query(statement, parameter);
	}


	/**
	 * 执行增删改操作
	 * <p>
	 *
	 * @param statement
	 * @param parameter
	 * @return
	 */
	protected int update( MappedStatement statement, Object parameter ) {
		return db.update(statement, parameter);
	}


	/**
	 * 查询语句
	 * <p>
	 *
	 * @param id
	 * @param sqlSource
	 * @return
	*/
	protected MappedStatement getQueryStatement( String id, SqlSource sqlSource ) {
		MappedStatement.Builder statement = new MappedStatement.Builder(configuration, id, sqlSource,
				SqlCommandType.SELECT);
		statement.resultMaps(tableMeta.getResultMapList());
		return statement.build();
	}


	/**
	 * 更新语句
	 * <p>
	 *
	 * @param id
	 * @param sqlSource
	 * @param type
	 * @return
	*/
	protected MappedStatement getUpdateStatement( String id, SqlSource sqlSource, SqlCommandType type ) {
		MappedStatement.Builder statement = new MappedStatement.Builder(configuration, id, sqlSource, type);

		return statement.build();
	}


	@Override
	public Table<TABLE, ID> getTable() {

		return tableMeta.getTable();

	}
}
