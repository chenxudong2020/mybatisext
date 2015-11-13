package com.ext_ext.mybatisext.activerecord.config;

/**
 *
 * 数据库表字段对应适配器
 *
 */
public interface ColumnMappingAdaptor {

	/**
	 * @param tableName
	 *            数据库表名称
	 * @param column
	 *            数据库字段名称
	 * @return 映射的java字段名称
	 */
	public String adaptor( String column );
}
