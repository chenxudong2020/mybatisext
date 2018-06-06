package com.ext.mybatisext.interceptor;

public interface MyBatisInterceptor {

	public Object invoke(MyBatisInvocation handler) throws Throwable;
}
