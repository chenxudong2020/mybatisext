package com.ext.mybatisext.activerecord.impl;

import java.sql.Array;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

import com.alibaba.fastjson.JSONObject;
import com.ext.mybatisext.activerecord.Connector;
import com.ext.mybatisext.activerecord.DB;
import com.ext.mybatisext.activerecord.ext.ConfigurationExt;
import com.ext.mybatisext.helper.ArrayTypeHandler;
import com.ext.mybatisext.helper.JSONTypeHandler;
import com.ext.mybatisext.helper.SmartDate;
import com.ext.mybatisext.helper.SmartDateTypeHandler;
import com.ext.mybatisext.helper.SqlSessionFactoryHolder;
import com.ext.mybatisext.plugin.IndexingPlugin;
import com.ext.mybatisext.plugin.SQLPrintPlugin;

public class ConnectorImpl implements Connector {

	private final SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();


	@Override
	public DB open() {
		// spring配置
		SqlSessionFactory factory = SqlSessionFactoryHolder.getSqlSessionFactory();
		if ( factory == null ) {
			throw new RuntimeException("请初始化SqlSessionFactoryBeanExt");
		}
		DB db = new DBImpl(factory).getDBProxy();
		return db;
	}


	@Override
	public DB open( String driver, String url, String username, String password ) {
		JdbcTransactionFactory factory = new JdbcTransactionFactory();
		PooledDataSource pool = new PooledDataSource(driver, url, username, password);
		pool.setPoolPingEnabled(true);
		pool.setPoolPingQuery(" select 'x' ");
		DB db = new DBImpl(getSessionFactory(JdbcTransactionFactory.class.getName(), factory, pool)).getDBProxy();
		return db;
	}


	@Override
	public DB open( DataSource pool ) {
		SpringManagedTransactionFactory factory = new SpringManagedTransactionFactory();
		DB db = new DBImpl(getSessionFactory(SpringManagedTransactionFactory.class.getName(), factory, pool)).getDBProxy();
		return db;
	}


	private SqlSessionFactory getSessionFactory( String id, TransactionFactory factory, DataSource ds ) {
		Environment environment = new Environment(id, factory, ds);
		ConfigurationExt configuration = new ConfigurationExt(environment);
		//添加拦截器
		configuration.addInterceptor(new SQLPrintPlugin());
		configuration.addInterceptor(new IndexingPlugin());
		configuration.getTypeHandlerRegistry().register(java.util.Date.class, SmartDateTypeHandler.class);
		configuration.getTypeHandlerRegistry().register(java.sql.Date.class, SmartDateTypeHandler.class);
		configuration.getTypeHandlerRegistry().register(SmartDate.class, SmartDateTypeHandler.class);
		//configuration.getTypeHandlerRegistry().register(List.class, ListTypeHandler.class);
		configuration.getTypeHandlerRegistry().register(JSONObject.class, JSONTypeHandler.class);
		configuration.getTypeHandlerRegistry().register(Array.class, ArrayTypeHandler.class);
		return sqlSessionFactoryBuilder.build(configuration);
	}


	@Override
	public DB open( SqlSessionFactory sessionFactory ) {
		DB db = new DBImpl(sessionFactory).getDBProxy();
		return db;
	}

}
