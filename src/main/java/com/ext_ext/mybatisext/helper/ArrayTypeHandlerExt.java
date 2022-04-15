package com.ext.mybatisext.helper;


import java.lang.reflect.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.ibatis.type.ArrayTypeHandler;
import org.apache.ibatis.type.JdbcType;


/**
* 列表数组支持
* <p>

* @author 宋汝波
* @date 2014年12月9日
* @version 1.0.0
*/
public class ArrayTypeHandlerExt extends ArrayTypeHandler {

	public ArrayTypeHandlerExt() {
		super();
	}


	@Override
	public void setNonNullParameter( PreparedStatement ps, int i, Object parameter, JdbcType jdbcType ) throws SQLException {
		if ( parameter instanceof java.sql.Array ) {
			ps.setArray(i, (java.sql.Array) parameter);
		} else {
			int length = Array.getLength(parameter);
			Object[] array = new Object[length];
			for ( int index = 0 ; index < length ; index++ ) {
				Object obj = Array.get(parameter, index);
				array[index] = obj;
			}
			ps.setArray(i, ArrayHelper.convertToPgSqlArray(array, parameter.getClass()));
		}

	}

}
