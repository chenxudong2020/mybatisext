package com.github.mybatisext.helper;

import org.apache.ibatis.session.SqlSessionFactory;

public abstract class SqlSessionFactoryHolder {

	private static SqlSessionFactory sqlSessionFactory;


	public static void setSqlSessionFactory( SqlSessionFactory sqlSessionFactory ) {
		SqlSessionFactoryHolder.sqlSessionFactory = sqlSessionFactory;
	}


	public static SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}


}
