
package com.ext.mybatisext.helper;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


/**
 * <p>

 * @author   宋汝波
 * @date	 2016年5月18日 
 * @version  1.0.0	 
 */
public class ObjectArray<T> implements Array {

	T[] result = null;

	String baseType;


	public ObjectArray( T[] result, String baseType ) {
		this.result = result;
		this.baseType = baseType;
	}


	@Override
	public String getBaseTypeName() throws SQLException {

		return baseType;

	}


	@Override
	public int getBaseType() throws SQLException {

		return 0;

	}


	@Override
	public Object getArray() throws SQLException {

		return null;

	}


	@Override
	public Object getArray( Map<String, Class<?>> map ) throws SQLException {


		return null;

	}


	@Override
	public Object getArray( long index, int count ) throws SQLException {


		return null;

	}


	@Override
	public Object getArray( long index, int count, Map<String, Class<?>> map ) throws SQLException {


		return null;

	}


	@Override
	public ResultSet getResultSet() throws SQLException {


		return null;

	}


	@Override
	public ResultSet getResultSet( Map<String, Class<?>> map ) throws SQLException {


		return null;

	}


	@Override
	public ResultSet getResultSet( long index, int count ) throws SQLException {


		return null;

	}


	@Override
	public ResultSet getResultSet( long index, int count, Map<String, Class<?>> map ) throws SQLException {


		return null;

	}


	@Override
	public void free() throws SQLException {

		result = null;
	}


	@Override
	public String toString() {
		if ( result == null ) {
			return "{}";
		}
		StringBuilder str = new StringBuilder("{");
		for ( int i = 0 ; i < result.length ; i++ ) {
			if ( i != 0 ) {
				str.append(",");
			}
			str.append(result[i]);
		}
		str.append("}");
		return str.toString();

	}

}

