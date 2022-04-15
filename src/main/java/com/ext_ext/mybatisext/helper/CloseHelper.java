package com.ext_ext.mybatisext.helper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.ibatis.transaction.Transaction;


/**
 * <p>

 * @author   宋汝波
 * @date	 2015年8月21日 
 * @version  1.0.0	 
 */
public abstract class CloseHelper {

	public static void close( Transaction trans, Statement st, ResultSet rs ) {
		try {
			if ( st != null ) {
				st.close();
			}

			if ( rs != null ) {
				rs.close();
			}
			if ( trans != null ) {
				trans.close();
			}
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
	}
}
