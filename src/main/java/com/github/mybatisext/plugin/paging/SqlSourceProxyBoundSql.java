package com.github.mybatisext.plugin.paging;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;


/**
 * 扩展兼容分页
 * <p>

 * @author   宋汝波
 * @date	 2014年11月24日 
 * @version  1.0.0	 
 */
public class SqlSourceProxyBoundSql implements SqlSource {


	private final BoundSql boundSql;


	public SqlSourceProxyBoundSql( BoundSql boundSql ) {
		this.boundSql = boundSql;
	}


	@Override
	public BoundSql getBoundSql( Object parameterObject ) {
		return boundSql;
	}

}
