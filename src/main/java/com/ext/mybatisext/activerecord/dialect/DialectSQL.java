package com.ext.mybatisext.activerecord.dialect;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * <p>
 * 
 * @author 宋汝波
 * @date 2015年8月17日
 * @version 1.0.0
 */
public abstract class DialectSQL {

	public enum Dialect {
		MYSQL, SQL_SERVER, ORACLE, HSQLDB, POSTGRESQL, DB2;
	}


	// 分页的sql
	public abstract String getPagingSQL( int start, int size, String sql );


	// 分页取总条数的sql
	public abstract String getPagingCountSQL( String sql );


	public static Dialect whichDB( DatabaseMetaData dbmd ) throws SQLException {
		String dbName = dbmd.getDatabaseProductName().toLowerCase();
		if ( dbName.contains("mysql") ) {
			return Dialect.MYSQL;
		} else if ( dbName.contains("postgresql") ) {
			return Dialect.POSTGRESQL;
		} else if ( dbName.contains("hsql") ) {
			return Dialect.HSQLDB;
		} else if ( dbName.contains("microsoft") ) {
			return Dialect.SQL_SERVER;
		}
		return null;
	}
}
