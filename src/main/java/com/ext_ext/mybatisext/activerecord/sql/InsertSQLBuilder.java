package com.ext_ext.mybatisext.activerecord.sql;

import java.util.Map;

import com.ext_ext.mybatisext.activerecord.meta.TableMeta;

public abstract class InsertSQLBuilder {

	public static <TABLE, ID> String buildInsertListSQL( TableMeta<TABLE, ID> tableMeta ) {

		Map<String, String> mapping = tableMeta.getPropertyColumnMapping();
		//String[] properties = PropertyHelper.getProperties(cls);
		StringBuilder sql = new StringBuilder();
		sql.append("<script>");
		sql.append("INSERT INTO ");
		sql.append(tableMeta.getName());
		sql.append(" <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\"> ");
		for ( Map.Entry<String, String> property : mapping.entrySet() ) {
			sql.append(property.getValue());
			sql.append(",");
		}
		sql.append(" </trim> ");
		sql.append(" VALUES ");
		sql.append(" <foreach collection=\"list\" item=\"item\" separator=\",\"> ");

		boolean comma = false;
		sql.append("(");
		for ( Map.Entry<String, String> property : mapping.entrySet() ) {
			if ( comma ) {
				sql.append(",");
			}
			sql.append("#{item.");
			sql.append(property.getKey());
			sql.append("}");
			comma = true;
		}
		sql.append(")");
		sql.append(" </foreach> ");
		sql.append(" </script>");

		return sql.toString();
	}


	public static <TABLE, ID> String buildInsertEntitySQL( TableMeta<TABLE, ID> tableMeta ) {
		Map<String, String> mapping = tableMeta.getPropertyColumnMapping();
		//String[] properties = PropertyHelper.getProperties(cls);
		StringBuilder sql = new StringBuilder();
		sql.append("<script>");
		sql.append("INSERT INTO ");
		sql.append(tableMeta.getName());
		sql.append(" <trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\"> ");

		for ( Map.Entry<String, String> property : mapping.entrySet() ) {
			sql.append("<if test=\"" + property.getValue() + "!=null\">");
			sql.append(property.getValue());
			sql.append(",");
			sql.append("</if>");


		}
		sql.append(" </trim> ");
		sql.append("<trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">");
		for ( Map.Entry<String, String> property : mapping.entrySet() ) {
			sql.append("<if test=\"" + property.getValue() + "!=null\">");
			sql.append("#{");
			sql.append(property.getValue());
			sql.append("},");
			sql.append("</if>");
		}
		sql.append("</trim>");
		sql.append(" </script>");

		return sql.toString();
	}


}
