package com.ext_ext.mybatisext.interceptor;

import java.util.List;

import com.ext_ext.mybatisext.interceptor.MyBatisInterceptor;

public class DefaultMyBatisExtFactory implements MyBatisInterceptorFactory {

	private List<MyBatisInterceptor> interceptors;


	@Override
	public MyBatisInterceptor[] getMyBatisInterceptor() {

		return getInterceptors().toArray(new MyBatisInterceptor[0]);
	}


	public List<MyBatisInterceptor> getInterceptors() {
		return interceptors;
	}


	public void setInterceptors( List<MyBatisInterceptor> interceptors ) {
		this.interceptors = interceptors;
	}

}