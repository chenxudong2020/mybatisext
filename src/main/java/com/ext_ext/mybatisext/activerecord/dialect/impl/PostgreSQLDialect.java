package com.ext.mybatisext.activerecord.dialect.impl;

import com.ext.mybatisext.activerecord.dialect.DialectSQL;

public class PostgreSQLDialect extends DialectSQL {

    @Override
    public String getPagingSQL(int start, int size, String sql) {
        StringBuilder sqlBuilder = new StringBuilder(sql.length() + 120);
        sqlBuilder.append("SELECT * FROM ( ");
        sqlBuilder.append(sql);
        sqlBuilder.append(" ) TMP_PAGE");
        sqlBuilder.append(" OFFSET ");
        sqlBuilder.append(start);
        sqlBuilder.append(" LIMIT ");
        sqlBuilder.append(size);
        return sqlBuilder.toString();

    }


    @Override
    public String getPagingCountSQL(String sql) {
        StringBuilder countSql = new StringBuilder("select count(*) as cnt from ");
        countSql.append("(");
        countSql.append(sql);
        countSql.append(")");
        countSql.append(" count_table ");
        return countSql.toString();

    }
}
