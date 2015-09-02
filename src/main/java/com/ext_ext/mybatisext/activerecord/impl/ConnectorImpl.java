package com.ext_ext.mybatisext.activerecord.impl;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;

import com.ext_ext.mybatisext.activerecord.Connector;
import com.ext_ext.mybatisext.activerecord.DB;
import com.ext_ext.mybatisext.activerecord.proxy.DBProxy;
import com.ext_ext.mybatisext.helper.SqlSessionFactoryHolder;

public class ConnectorImpl implements Connector {

	private final SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();


	@Override
	public DB open() {
		// spring配置
		SqlSessionFactory factory = SqlSessionFactoryHolder.getSqlSessionFactory();
		if ( factory == null ) {
			throw new RuntimeException("请初始化SqlSessionFactoryBeanExt");
		}
		DB db = new DBImpl(factory);
		return DBProxy.getDBProxy(db);
	}


	@Override
	public DB open( String driver, String url, String username, String password ) {
		JdbcTransactionFactory factory = new JdbcTransactionFactory();
		PooledDataSource pool = new PooledDataSource(driver, url, username, password);
		DB db = new DBImpl(getSessionFactory(JdbcTransactionFactory.class.getName(), factory, pool));
		return DBProxy.getDBProxy(db);
	}


	@Override
	public DB open( DataSource pool ) {
		ManagedTransactionFactory factory = new ManagedTransactionFactory();
		DB db = new DBImpl(getSessionFactory(ManagedTransactionFactory.class.getName(), factory, pool));
		return DBProxy.getDBProxy(db);
	}


	private SqlSessionFactory getSessionFactory( String id, TransactionFactory factory, DataSource ds ) {
		Environment environment = new Environment(id, factory, ds);
		Configuration configuration = new Configuration(environment);

		return sqlSessionFactoryBuilder.build(configuration);
	}

}