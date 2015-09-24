package com.ext_ext.mybatisext.helper;

import java.util.Collections;
import java.util.List;


/**
 * <p>

 * @author   宋汝波
 * @date	 2015年8月25日 
 * @version  1.0.0	 
 */
public class PageImpl<T> implements Page<T> {

	int pageNo = 1;

	int pageSize = 10;

	List<T> data = Collections.emptyList();

	int count;


	public PageImpl() {

	}


	public PageImpl( int pageNo, int pageSize ) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}


	@Override
	public int getPageNo() {

		return pageNo;

	}


	@Override
	public int getPageSize() {

		return pageSize;

	}


	@Override
	public void setRecords( List<T> data ) {
		this.data = data;

	}


	@Override
	public void setCount( int count ) {
		this.count = count;

	}


	@Override
	public List<T> getRecords() {

		return data;

	}


	@Override
	public int getCount() {

		return count;

	}


	@Override
	public int getPages() {

		return (count + pageSize - 1) / pageSize;

	}

}
