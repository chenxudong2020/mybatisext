/**
 * ColumnMappingAdaptorImpl.java com.ext_ext.mybatisext.test Copyright (c) 2015,
 * 北京微课创景教育科技有限公司版权所有.
 */

package com.ext_ext.mybatisext.test;

import com.ext_ext.mybatisext.activerecord.config.ColumnMappingAdaptor;


/**
 * <p>

 * @author   宋汝波
 * @date	 2015年11月13日 
 * @version  1.0.0	 
 */
public class ColumnMappingAdaptorImpl implements ColumnMappingAdaptor {

	@Override
	public String adaptor( String column ) {

		return column.replace("_", "");

	}

}