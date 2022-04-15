package com.ext_ext.mybatisext.activerecord;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;

public interface Connector {

	// 根据sping配置的数据源打开
	public DB open();

	// 根据具体session工厂创建
	public DB open(SqlSessionFactory sessionFactory);

	// 默认数据源打开，自带的数据源
	public DB open(String driver, String url, String username, String password);

	// 指定数据源打开
	public DB open(DataSource pool);

}
