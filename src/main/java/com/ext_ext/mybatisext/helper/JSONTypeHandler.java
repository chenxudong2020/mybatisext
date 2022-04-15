package com.ext_ext.mybatisext.helper;


import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.alibaba.fastjson.JSONObject;


/**
* JSON处理
* <p>

* @author 宋汝波
* @date 2014年12月9日
* @version 1.0.0
*/
public class JSONTypeHandler extends BaseTypeHandler<JSONObject> {


	@Override
	public void setNonNullParameter( PreparedStatement ps, int i, JSONObject parameter, JdbcType jdbcType ) throws SQLException {

		//ps.setArray(i, ArrayHelper.convertToPgSqlArray(parameter.toArray()));

	}


	@Override
	public JSONObject getNullableResult( ResultSet rs, String columnName ) throws SQLException {

		Array array = rs.getArray(columnName);
		if ( array != null ) {
			array.getArray();
			//return new Date(sqlTimestamp.getTime());
		}
		return null;
	}


	@Override
	public JSONObject getNullableResult( ResultSet rs, int columnIndex ) throws SQLException {

		Array array = rs.getArray(columnIndex);
		if ( array != null ) {
			array.getArray();
		}
		return null;

	}


	@Override
	public JSONObject getNullableResult( CallableStatement cs, int columnIndex ) throws SQLException {

		Array array = cs.getArray(columnIndex);
		if ( array != null ) {
			array.getArray();
		}
		return null;

	}
}
