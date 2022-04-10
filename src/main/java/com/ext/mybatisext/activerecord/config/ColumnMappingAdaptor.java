package com.ext.mybatisext.activerecord.config;

/**
 *
 * 数据库表字段对应适配器
 *
 */
public interface ColumnMappingAdaptor {

	/**
	 *
	 * @param beanClass 表实体
	 * @param column 数据库字段
	 * @return  java属性
	 */
	public String adaptor(Class beanClass,String column );


}
