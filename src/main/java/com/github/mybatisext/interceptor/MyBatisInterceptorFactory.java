package com.github.mybatisext.interceptor;

import com.github.mybatisext.interceptor.MyBatisInterceptor;

public interface MyBatisInterceptorFactory {

	//拦截器,用于缓存
	public MyBatisInterceptor[] getMyBatisInterceptor();
	//orm映射关系
}
