package com.ext_ext.mybatisext.interceptor;

import com.ext_ext.mybatisext.interceptor.MyBatisInterceptor;

public interface MyBatisInterceptorFactory {

	//拦截器,用于缓存
	public MyBatisInterceptor[] getMyBatisInterceptor();
	//orm映射关系
}
