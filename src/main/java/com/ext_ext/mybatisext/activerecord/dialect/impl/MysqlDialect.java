package com.ext_ext.mybatisext.activerecord.dialect.impl;

import com.ext_ext.mybatisext.activerecord.dialect.DialectSQL;

public class MysqlDialect extends DialectSQL {


	@Override
	public String getPagingSQL( int start, int size, String sql ) {
		return new StringBuilder(sql).append(" limit ").append(start).append(",").append(size).toString();


	}


	@Override
	public String getPagingCountSQL( String sql ) {

		return getCountSql(sql);

	}


	/**
	 * 拼接获取条数的sql语句
	 * <p>
	 *
	 * @param sqlPrimary
	 */
	protected String getCountSql( String sqlPrimary ) {
		StringBuilder return_sql = new StringBuilder("SELECT COUNT(1) AS cnt ");
		String sqlUse = sqlPrimary.replaceAll("[\\s]+", " ");
		String upperString = sqlUse.toUpperCase();
		int order_by = upperString.lastIndexOf(" ORDER BY ");
		if ( order_by > -1 ) {
			sqlUse = sqlUse.substring(0, order_by);
		}
		if ( upperString.contains(" GROUP BY ") || upperString.contains(" DISTINCT ") ) {
			return_sql.append(" FROM ( ");
			return_sql.append(sqlPrimary);
			return_sql.append(" ) temp_table ");
			return return_sql.toString();
		}
		String[] paramsAndMethod = sqlUse.split("\\s");
		int count = 0;
		int index = 0;
		for ( int i = 0 ; i < paramsAndMethod.length ; i++ ) {
			String upper = paramsAndMethod[i].toUpperCase();
			if ( upper.length() == 0 ) {
				continue;
			}
			if ( upper.equals("SELECT") ) {
				count++;
			} else if ( upper.equals("FROM") ) {
				count--;
			}
			if ( count == 0 ) {
				index = i;
				break;
			}
		}

		StringBuilder common_count = new StringBuilder();
		for ( int j = index ; j < paramsAndMethod.length ; j++ ) {
			common_count.append(" ");
			common_count.append(paramsAndMethod[j]);
		}

		return return_sql.append(common_count).toString();
	}


}
