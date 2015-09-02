package com.ext_ext.mybatisext.test;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.ext_ext.mybatisext.helper.Page;

/**
 * Page is not a domain object but is used to store and fetch page information.
 *
 * @author king.zhu
 */
@SuppressWarnings({ "serial" })
public class PageImpl<T> implements Page<T>, Serializable {

	private static final int DEF_PAGE_VIEW_SIZE = 10;

	/** 当前页 */
	private int page;

	/** 当前页显示记录条数 */
	private int pageSize;

	/** 取得查询总记录数 */
	private int count;

	List<T> records = Collections.emptyList();


	/**
	 * 根据当前显示页与每页显示记录数设置查询信息初始对象
	 * @param page 当前显示页号
	 * @param pageSize 当前页显示记录条数
	 */
	public PageImpl( int page ) {
		this(page, DEF_PAGE_VIEW_SIZE);
	}


	public PageImpl( Integer page, Integer pageSize ) {
		this.page = (null == page || page <= 0) ? 1 : page;
		this.pageSize = (null == pageSize || pageSize <= 0) ? DEF_PAGE_VIEW_SIZE : pageSize;
	}


	/**
	 * 取得当前显示页号
	 * @return 当前显示页号
	 */
	public int getPage() {
		return page <= 0 ? 1 : page;
	}


	@Override
	public int getPageNo() {
		return page <= 0 ? 1 : page;
	}


	public boolean getHasPrevious() {
		return page > 1;
	}


	public boolean getHasNext() {
		return page < getPages();
	}


	/**
	 * 设置当前页
	 * @param page 当前页
	 */
	public void setPage( int page ) {
		this.page = page;
	}


	public void setPageNo( int page ) {
		this.page = page;
	}


	/**
	 * 取得当前显示页号最多显示条数
	 * @return 当前显示页号最多显示条数
	 */
	@Override
	public int getPageSize() {
		return pageSize <= 0 ? DEF_PAGE_VIEW_SIZE : pageSize;
	}


	/**
	 * 设置当前页显示记录条数
	 * @param pageSize 当前页显示记录条数
	 */
	public void setPageSize( int pageSize ) {
		this.pageSize = pageSize;
	}


	/**
	 * 取得查询取得记录总数
	 * @return 取得查询取得记录总数
	 */
	@Override
	public int getCount() {
		return count;
	}


	/**
	 * 设置查询取得记录总数
	 * @param count 查询取得记录总数
	 */
	@Override
	public void setCount( int count ) {
		this.count = count < 0 ? 0 : count;
		if ( this.count == 0 ) {
			page = 0;
			return;
		}

	}


	/**
	 * 取得当前查询总页数
	 * @return 当前查询总页数
	 */
	@Override
	public int getPages() {
		return (count + getPageSize() - 1) / getPageSize();
	}


	/**
	 * 取得起始显示记录号
	 * @return 起始显示记录号
	 */
	public int getStartNo() {
		return (getPage() - 1) * getPageSize() + 1;
	}


	/**
	 * 取得结束显示记录号
	 * @return 结束显示记录号
	 */
	public int getEndNo() {
		return Math.min(getPage() * getPageSize(), count);
	}


	/**
	 * 取得前一显示页码
	 * @return 前一显示页码
	 */
	public int getPrePageNo() {
		return Math.max(getPage() - 1, 1);
	}


	/**
	 * 取得后一显示页码
	 * @return 后一显示页码
	 */
	public int getNextPageNo() {
		return Math.min(getPage() + 1, getPages());
	}


	@Override
	public List<T> getRecords() {
		return records;
	}


	@Override
	public void setRecords( List<T> records ) {
		this.records = records;
	}


}
