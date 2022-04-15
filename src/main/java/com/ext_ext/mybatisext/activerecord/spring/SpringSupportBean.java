package com.ext.mybatisext.activerecord.spring;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.ext.mybatisext.activerecord.DB;
import com.ext.mybatisext.activerecord.MybatisExt;


/**
 * 支持spring中注入DB对象
 * <p>

 * @author   宋汝波
 * @date	 2015年11月10日 
 * @version  1.0.0	 
 */
public class SpringSupportBean implements FactoryBean<DB>, InitializingBean {

	DataSource dataSource;

	SqlSessionFactory sessionFactory;

	DB db;


	@Override
	public DB getObject() throws Exception {

		if ( this.db != null ) {
			return this.db;
		}
		if ( dataSource != null ) {
			this.db = MybatisExt.open(dataSource);
		}
		if ( sessionFactory != null ) {
			this.db = MybatisExt.open(sessionFactory);
		}
		return this.db;

	}


	@Override
	public Class<?> getObjectType() {

		return DB.class;

	}


	@Override
	public boolean isSingleton() {

		return true;

	}


	public void setDataSource( DataSource dataSource ) {
		this.dataSource = dataSource;
	}


	public void setSessionFactory( SqlSessionFactory sessionFactory ) {
		this.sessionFactory = sessionFactory;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		if ( sessionFactory == null && dataSource == null ) {
			throw new RuntimeException("请注入sessionFactory或者dataSource");
		}
	}
}
