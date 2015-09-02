package com.ext_ext.mybatisext.activerecord;

import javax.sql.DataSource;

import com.ext_ext.mybatisext.activerecord.config.ColumnMappingAdaptor;
import com.ext_ext.mybatisext.activerecord.dialect.DialectSQL;
import com.ext_ext.mybatisext.activerecord.impl.ConnectorImpl;

/**
 * 入口类
 * 
 * @author rubo
 *
 */
public abstract class MybatisExt {

	// 属性和表列之间的映射关系
	public static ColumnMappingAdaptor adaptor;

	//手动指定方言
	public static DialectSQL dialectSQL;

	// 连接
	private static final Connector connector = new ConnectorImpl();


	/**
	 * 根据sping配置的数据源打开,在配置了mybatis的完整配置后 使用SpringManagedTransaction,spring的标准配置.
	 * 需用SqlSessionFactoryBeanExt代替SqlSessionFactoryBean在spring中进行配置
	 * 
	 * @return
	 */
	public static DB open() {
		return connector.open();
	}


	/**
	 * 默认数据源打开，自带的数据源,适合导数据,自动提交,使用JdbcTransactionFactory
	 * 
	 * @param driver
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	public static DB open( String driver, String url, String username, String password ) {
		return connector.open(driver, url, username, password);
	}


	/**
	 * 指定数据源打开 ,使用ManagedTransaction，手动指定事务管理
	 * 
	 * @param pool
	 * @return
	 */
	public static DB open( DataSource pool ) {
		return connector.open(pool);
	}


	/**
	 * 添加映射适配,默认可以不设置,属性名称和表列名称相同,不区分大小写
	 * 
	 * @param adaptor
	 */
	public static void setColumnMappingAdaptor( ColumnMappingAdaptor adaptor ) {
		MybatisExt.adaptor = adaptor;
	}


	/**
	 * 指定方言
	 * <p>
	 *
	 * @param dialectSQL
	*/
	public static void setDialectSQL( DialectSQL dialectSQL ) {
		MybatisExt.dialectSQL = dialectSQL;
	}
}
