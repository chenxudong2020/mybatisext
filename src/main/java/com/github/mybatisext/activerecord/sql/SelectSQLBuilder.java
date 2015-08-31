package com.github.mybatisext.activerecord.sql;

import java.util.Map;

import com.github.mybatisext.activerecord.meta.TableMeta;

public abstract class SelectSQLBuilder {

	// 动态列名称定义
	public static final String DYNAMIC_COLUMN = "_dynamic_column";


	public static String buildSelectById( TableMeta<?, ?> tableMeta ) {

		StringBuilder str = buildSelect(tableMeta);
		str.append(" WHERE ");
		str.append(tableMeta.getIdName());
		str.append(" = ");
		str.append(" #{");
		str.append(tableMeta.getIdName());
		str.append("} ");

		return str.toString();
	}


	public static String buildSelectByOne( TableMeta<?, ?> tableMeta, String field ) {

		StringBuilder str = buildSelect(tableMeta);
		str.append(" WHERE ");
		str.append(tableMeta.getColumnName(field));
		str.append(" = ");
		str.append(" #{");
		str.append(field);
		str.append("} ");

		return str.toString();
	}


	public static String buildDynamicColumn( TableMeta<?, ?> tableMeta ) {
		StringBuilder str = new StringBuilder(" ");
		boolean comma = false;
		for ( Map.Entry<String, String> p : tableMeta.getPropertyColumnMapping().entrySet() ) {
			if ( comma ) {
				str.append(" , ");
			}
			str.append(p.getValue());
			comma = true;
		}
		return str.toString();
	}


	public static String buildDynamicColumn( TableMeta<?, ?> tableMeta, String[] columns ) {
		StringBuilder str = new StringBuilder(" ");
		for ( int i = 0 ; i < columns.length ; i++ ) {
			if ( i != 0 ) {
				str.append(" , ");
			}
			str.append(tableMeta.getColumnName(columns[i]));
		}
		return str.toString();
	}


	private static StringBuilder buildSelect( TableMeta<?, ?> tableMeta ) {
		StringBuilder str = new StringBuilder(" SELECT ");

		// 动态列查询
		str.append(" ${");
		str.append(DYNAMIC_COLUMN);
		str.append("} ");
		str.append(" FROM ");
		str.append(tableMeta.getName());

		return str;
	}


	public static String buildSelectListWithScript( TableMeta<?, ?> tableMeta ) {
		StringBuilder str = new StringBuilder("<script>");
		str.append(buildSelectList(tableMeta));
		str.append("</script>");
		return str.toString();
	}


	public static String buildSelectList( TableMeta<?, ?> tableMeta ) {
		StringBuilder str = new StringBuilder("");
		str.append(buildSelect(tableMeta));
		str.append(" <where> ");
		for ( Map.Entry<String, String> p : tableMeta.getPropertyColumnMapping().entrySet() ) {
			str.append(" <if test=\"" + p.getKey() + "!=null\"> ");

			str.append(" AND ");
			str.append(p.getValue());
			str.append(" = ");
			str.append(" #{");
			str.append(p.getKey());
			str.append("}");

			str.append(" </if> ");
		}
		str.append(" </where> ");
		return str.toString();
	}


	public static String buildSelectPaging( TableMeta<?, ?> tableMeta, int pageNo, int size ) {

		StringBuilder str = new StringBuilder("<script>");
		str.append(tableMeta.getDb().getDBMeta().getDialectSQL()
				.getPagingSQL(((pageNo < 1 ? 1 : pageNo) - 1) * size, size, buildSelectList(tableMeta)));

		str.append("</script>");

		return str.toString();
	}
}
