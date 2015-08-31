/**
 * Statement.java com.github.mybatisext.activerecord.statement Copyright (c)
 * 2015, 北京微课创景教育科技有限公司版权所有.
 */

package com.github.mybatisext.activerecord.statement;

import com.github.mybatisext.activerecord.Table;


/**
 * 通用接口
 * <p>

 * @author   宋汝波
 * @date	 2015年8月24日 
 * @version  1.0.0	 
 */
public interface Statement<TABLE, ID> {


	/**
	 * 获取表对象
	 * <p>
	 *
	 * @return
	*/
	public Table<TABLE, ID> getTable();
}
