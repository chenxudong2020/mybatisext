package com.github.mybatisext.activerecord.sql;

import java.util.Map;

import com.github.mybatisext.activerecord.meta.TableMeta;

public abstract class UpdateSQLBuilder {

	public static String buildUpdateById( TableMeta<?, ?> tableMeta, boolean isAll ) {
		StringBuilder str = new StringBuilder(" UPDATE ");
		str.append(tableMeta.getName());

		str.append(sets(tableMeta, isAll));

		str.append(" WHERE ");
		str.append(tableMeta.getIdName());
		str.append("=");
		str.append(" #{");
		str.append(tableMeta.getPropertyName(tableMeta.getIdName()));
		str.append("} ");
		return str.toString();
	}


	public static String buildUpdateByOne( TableMeta<?, ?> tableMeta, String field, String operator, boolean isAll ) {
		StringBuilder str = new StringBuilder(" UPDATE ");
		str.append(tableMeta.getName());

		str.append(sets(tableMeta, isAll));

		str.append(" WHERE ");
		str.append(tableMeta.getColumnName(field));
		str.append(operator);
		str.append(" #{");
		str.append(field);
		str.append("} ");
		return str.toString();
	}


	public static String appendScript( String sql ) {
		StringBuilder str = new StringBuilder("<script>");
		str.append(sql);
		str.append("</script>");
		return str.toString();
	}


	private static String sets( TableMeta<?, ?> tableMeta, boolean isAll ) {
		StringBuilder str = new StringBuilder("");
		str.append(" <set> ");
		for ( Map.Entry<String, String> field : tableMeta.getPropertyColumnMapping().entrySet() ) {
			if ( field.getValue().equals(tableMeta.getIdName()) ) {
				// 不更新主键
				continue;
			}
			if ( !isAll ) {
				str.append(" <if test=\"" + field.getKey() + "!=null\"> ");
			}
			str.append(field.getValue());
			str.append("=");
			str.append("#{");
			str.append(field.getKey());
			str.append("},");
			if ( !isAll ) {
				str.append(" </if> ");
			}
		}
		str.append(" </set> ");

		return str.toString();
	}


	public static String buildUpdate( TableMeta<?, ?> tableMeta ) {
		StringBuilder str = new StringBuilder("");
		str.append(" UPDATE ");
		str.append(tableMeta.getName());

		str.append(sets(tableMeta, false));

		str.append(" <where> ");
		for ( Map.Entry<String, String> field : tableMeta.getPropertyColumnMapping().entrySet() ) {
			str.append(" <if test=\"where_" + field.getKey() + "!=null\"> ");

			str.append(" AND ");
			str.append(field.getValue());
			str.append(" = ");
			//避免冲突,加前缀
			str.append(" #{where_");
			str.append(field.getKey());
			str.append("}");

			str.append(" </if> ");
		}
		str.append(" </where> ");
		return str.toString();
	}
}
