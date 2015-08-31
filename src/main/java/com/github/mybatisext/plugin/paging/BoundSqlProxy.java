package com.github.mybatisext.plugin.paging;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.Configuration;

import com.github.mybatisext.activerecord.dialect.DialectSQL;
import com.github.mybatisext.helper.Page;


/**
 * BoundSql代理
 * <p>

 * @author   宋汝波
 * @date	 2014年11月25日
 * @version  1.0.0
 */
public class BoundSqlProxy extends BoundSql {

	private final Page<?> page;

	private final BoundSql proxy;

	DialectSQL dialect;


	public BoundSqlProxy( Configuration configuration, Page<?> page, BoundSql proxy, DialectSQL dialect ) {

		super(configuration, proxy.getSql(), proxy.getParameterMappings(), proxy.getParameterObject());
		this.page = page;
		this.proxy = proxy;
		this.dialect = dialect;
	}


	@Override
	public String getSql() {
		int start = (page.getPageNo() - 1) * page.getPageSize();
		if ( dialect != null ) {
			return dialect.getPagingSQL(start, page.getPageSize(), proxy.getSql());
		}
		StringBuilder sb = new StringBuilder(proxy.getSql());
		sb.append(" LIMIT ").append(start).append(",").append(page.getPageSize());
		return sb.toString();

	}


	@Override
	public boolean hasAdditionalParameter( String name ) {

		return proxy.hasAdditionalParameter(name);

	}


	@Override
	public Object getAdditionalParameter( String name ) {

		return proxy.getAdditionalParameter(name);

	}


	@Override
	public void setAdditionalParameter( String name, Object value ) {

		proxy.setAdditionalParameter(name, value);

	}
}
