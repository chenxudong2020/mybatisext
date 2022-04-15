package com.ext.mybatisext.activerecord.dialect.impl;

import com.ext.mybatisext.activerecord.dialect.DialectSQL;

public class HsqldbDialect extends DialectSQL {


	@Override
	public String getPagingSQL( int start, int size, String sql ) {

		return new StringBuilder(sql).append(" limit ").append(start).append(",").append(size).toString();

	}


	@Override
	public String getPagingCountSQL( String sql ) {

		StringBuilder countSql = new StringBuilder("select count(*) as cnt from ");
		countSql.append("(");
		countSql.append(sql);
		countSql.append(")");
		countSql.append(" count_table ");
		return countSql.toString();

	}

}
