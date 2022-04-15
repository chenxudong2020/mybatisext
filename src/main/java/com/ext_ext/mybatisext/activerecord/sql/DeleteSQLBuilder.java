package com.ext.mybatisext.activerecord.sql;

import java.util.Map;

import com.ext.mybatisext.activerecord.meta.TableMeta;

public abstract class DeleteSQLBuilder {

	public static String buildDeleteById( TableMeta<?, ?> tableMeta ) {
		StringBuilder str = new StringBuilder(" DELETE FROM ");
		str.append(tableMeta.getName());
		str.append(" WHERE ");
		str.append(tableMeta.getIdName());
		str.append("=");
		str.append(" #{");
		str.append(tableMeta.getPropertyName(tableMeta.getIdName()));
		str.append("} ");
		return str.toString();
	}


	public static String buildDeleteByOne( TableMeta<?, ?> tableMeta, String field, String operator ) {
		StringBuilder str = new StringBuilder(" DELETE FROM ");
		str.append(tableMeta.getName());
		str.append(" WHERE ");
		str.append(tableMeta.getColumnName(field));
		str.append(operator);
		str.append(" #{");
		str.append(field);
		str.append("} ");
		return str.toString();
	}


	public static String buildDeleteWithScript( TableMeta<?, ?> tableMeta ) {

		StringBuilder str = new StringBuilder("<script>");
		str.append(buildDelete(tableMeta));
		str.append("</script>");
		return str.toString();
	}


	public static String buildDelete( TableMeta<?, ?> tableMeta ) {
		StringBuilder str = new StringBuilder("");
		str.append(" DELETE FROM ");
		str.append(tableMeta.getName());
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
}
