package com.ext_ext.mybatisext.interceptor;

public interface MyBatisInterceptor {

	public Object invoke(MyBatisInvocation handler) throws Throwable;
}
