package com.ext.mybatisext.activerecord.meta;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import com.ext.mybatisext.activerecord.dialect.impl.PostgreSQLDialect;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSession.StrictMap;
import org.apache.ibatis.transaction.Transaction;

import com.ext.mybatisext.activerecord.MybatisExt;
import com.ext.mybatisext.activerecord.config.ColumnsMapping;
import com.ext.mybatisext.activerecord.dialect.DialectSQL;
import com.ext.mybatisext.activerecord.dialect.DialectSQL.Dialect;
import com.ext.mybatisext.activerecord.dialect.impl.HsqldbDialect;
import com.ext.mybatisext.activerecord.dialect.impl.MysqlDialect;
import com.ext.mybatisext.helper.CloseHelper;

/**
 * 数据库信息
 * <p>
 * 
 * @author 宋汝波
 * @date 2015年8月19日
 * @version 1.0.0
 */
public class DBMeta {

	protected static Log logger = LogFactory.getLog(DBMeta.class);

	//工厂
	protected SqlSessionFactory factory;

	//配置
	protected Configuration configuration;

	//方言
	protected DialectSQL dialectSQL;

	//列和属性的映射工具类
	protected ColumnsMapping columnMapping;

	//解析xml脚本的驱动类
	protected LanguageDriver driver = new XMLLanguageDriver();


	public DBMeta( SqlSessionFactory factory ) {
		this.factory = factory;
		this.configuration = factory.getConfiguration();
		this.columnMapping = new ColumnsMapping(this);
		this.dialectSQL = getDialectSQL();
	}


	/**
	 * 获取连接
	 * <p>
	 *
	 * @return
	*/
	public Transaction getTransaction() {
		Environment environment = configuration.getEnvironment();
		DataSource ds = environment.getDataSource();
		Transaction trans = environment.getTransactionFactory().newTransaction(ds, null, false);

		return trans;
	}


	public Configuration getConfiguration() {
		return configuration;
	}


	public SqlSessionFactory getSessionFactory() {
		return factory;
	}


	// 列映射，可以为空
	public ColumnsMapping getColumnsMapping() {
		return columnMapping;
	}


	// 数据库类型
	public DialectSQL getDialectSQL() {
		//优先使用配置
		if ( MybatisExt.dialectSQL != null ) {
			return MybatisExt.dialectSQL;
		}
		if ( dialectSQL != null ) {
			return dialectSQL;
		}
		DialectSQL dialect = null;
		Transaction trans = getTransaction();
		try {
			DatabaseMetaData dbmd = trans.getConnection().getMetaData();
			Dialect dia = DialectSQL.whichDB(dbmd);
			if ( dia == Dialect.MYSQL ) {
				dialect = new MysqlDialect();
			} else if ( dia == Dialect.HSQLDB ) {
				dialect = new HsqldbDialect();
			}
			else if ( dia == Dialect.POSTGRESQL ) {
				dialect = new PostgreSQLDialect();
			}
			trans.close();
		} catch ( SQLException e ) {
			logger.error("", e);
		} finally {
			CloseHelper.close(trans, null, null);
		}
		//设置
		MybatisExt.setDialectSQL(dialect);
		return dialect;
	}


	/**
	 * mybatis数组或list需要包装下
	 * <p>
	 *
	 * @param object
	 * @return
	*/
	public Object wrapCollection( final Object object ) {
		if ( object instanceof List ) {
			StrictMap<Object> map = new StrictMap<Object>();
			map.put("list", object);
			return map;
		} else if ( object != null && object.getClass().isArray() ) {
			StrictMap<Object> map = new StrictMap<Object>();
			map.put("array", object);
			return map;
		}
		return object;
	}


	public LanguageDriver getXMLDriver() {

		return driver;
	}


}
