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
