package com.ext.mybatisext.helper;

import java.math.BigDecimal;

public class ArrayHelper {

	public static <T> java.sql.Array convertToPgSqlArray( final T[] result, Class<?> type ) {
		if ( result == null || result.length < 1 ) {
			return null;
		}
		String baseType = getBaseType(type);
		ObjectArray<T> array = new ObjectArray<T>(result, baseType);
		return array;
	}


	private static String getBaseType( Class<?> type ) {
		String baseType = "";
		if ( type == int[].class || type == Integer[].class ) {
			baseType = "int4";
		} else if ( type == long[].class || type == Long[].class ) {
			baseType = "int8";
		} else if ( type == short[].class || type == Short[].class ) {
			baseType = "int2";
		} else if ( type == String[].class ) {
			baseType = "varchar";
		} else if ( type == boolean[].class || type == Boolean[].class ) {
			baseType = "bool";
		} else if ( type == BigDecimal[].class ) {
			baseType = "decimal";
		}
		return baseType;
	}


	public static <T> String toArrayString( final T[] result, Class<?> type ) {
		String baseType = getBaseType(type);
		ObjectArray<T> array = new ObjectArray<T>(result, baseType);
		return array.toString();
	}
}

