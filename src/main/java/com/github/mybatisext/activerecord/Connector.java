package com.github.mybatisext.activerecord;

import javax.sql.DataSource;

public interface Connector {

	// 根据sping配置的数据源打开
	public DB open();

	// 默认数据源打开，自带的数据源
	public DB open(String driver, String url, String username, String password);

	// 指定数据源打开
	public DB open(DataSource pool);

}
