package com.ext_ext.mybatisext;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;

import com.ext_ext.mybatisext.helper.ArrayTypeHandlerExt;
import com.ext_ext.mybatisext.helper.SmartDate;
import com.ext_ext.mybatisext.helper.SmartDateTypeHandler;
import com.ext_ext.mybatisext.helper.SqlSessionFactoryHolder;
import com.ext_ext.mybatisext.interceptor.MyBatisInterceptor;

/**
 * @author songrubo
 * @version 2013年11月30日 下午1:11:48 扩展功能,mapper文件自动加载,空resultMap自动配置,mapper拦截
 */
public class SqlSessionFactoryBeanExt extends SqlSessionFactoryBean {

	private static final Log logger = LogFactory.getLog(SqlSessionFactoryBeanExt.class);

	private final Class<?> superClass;

	private SqlSessionFactory sqlSessionFactory;

	private MyBatisInterceptor[] interceptors;


	public SqlSessionFactoryBeanExt() {
		superClass = SqlSessionFactoryBean.class;

	}


	public void setValue( String name, Object value ) {
		try {
			Field field = superClass.getDeclaredField(name);
			field.setAccessible(true);
			field.set(this, value);
		} catch ( Exception e ) {
			logger.error(e.getMessage(), e);
		}
	}


	@Override
	protected SqlSessionFactory buildSqlSessionFactory() throws IOException {
		SqlSessionFactory factory = super.buildSqlSessionFactory();

		try {
			Configuration config = factory.getConfiguration();
			Class<?> classConfig = Configuration.class;
			// 拦截
			Field field = classConfig.getDeclaredField("mapperRegistry");
			field.setAccessible(true);
			field.set(config, new MapperRegistryExt(config, interceptors));
			// 日期格式处理
			config.getTypeHandlerRegistry().register(java.util.Date.class, SmartDateTypeHandler.class);
			config.getTypeHandlerRegistry().register(java.sql.Date.class, SmartDateTypeHandler.class);
			config.getTypeHandlerRegistry().register(SmartDate.class, SmartDateTypeHandler.class);
			config.getTypeHandlerRegistry().register(String[].class, ArrayTypeHandlerExt.class);
			config.getTypeHandlerRegistry().register(Integer[].class, ArrayTypeHandlerExt.class);
			config.getTypeHandlerRegistry().register(BigDecimal[].class, ArrayTypeHandlerExt.class);
			config.getTypeHandlerRegistry().register(Double[].class, ArrayTypeHandlerExt.class);
			config.getTypeHandlerRegistry().register(Float[].class, ArrayTypeHandlerExt.class);
			config.getTypeHandlerRegistry().register(Long[].class, ArrayTypeHandlerExt.class);
		} catch ( Exception e ) {
			logger.error(e.getMessage(), e);
		}
		return factory;
	}


	@Override
	public SqlSessionFactory getObject() throws Exception {
		if ( sqlSessionFactory == null ) {
			sqlSessionFactory = buildSqlSessionFactory();
			setValue("sqlSessionFactory", sqlSessionFactory);
			SqlSessionFactoryHolder.setSqlSessionFactory(sqlSessionFactory);
		}
		return sqlSessionFactory;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		// 重写，解决初始化两次的bug
	}


	public MyBatisInterceptor[] getInterceptors() {
		return interceptors;
	}


	public void setInterceptors( MyBatisInterceptor[] interceptors ) {
		this.interceptors = interceptors;
	}


}
