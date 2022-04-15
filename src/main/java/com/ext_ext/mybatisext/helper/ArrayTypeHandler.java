package com.ext.mybatisext.helper;


import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;


/**
* 列表数组支持
* <p>

* @author 宋汝波
* @date 2014年12月9日
* @version 1.0.0
*/
public class ArrayTypeHandler extends BaseTypeHandler<Object> {

	public ArrayTypeHandler() {
		super();
	}


	@Override
	public void setNonNullParameter( PreparedStatement ps, int i, Object parameter, JdbcType jdbcType ) throws SQLException {
		ps.setArray(i, (Array) parameter);
	}


	@Override
	public Object getNullableResult( ResultSet rs, String columnName ) throws SQLException {
		Array array = rs.getArray(columnName);
		return array == null ? null : array.getArray();
	}


	@Override
	public Object getNullableResult( ResultSet rs, int columnIndex ) throws SQLException {
		Array array = rs.getArray(columnIndex);
		return array == null ? null : array.getArray();
	}


	@Override
	public Object getNullableResult( CallableStatement cs, int columnIndex ) throws SQLException {
		Array array = cs.getArray(columnIndex);
		return array == null ? null : array.getArray();
	}

}
