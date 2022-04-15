package com.ext.mybatisext;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;

import com.ext.mybatisext.interceptor.MyBatisInterceptor;
import com.ext.mybatisext.interceptor.MyBatisInvocation;
import com.ext.mybatisext.plugin.paging.PagingPlugin;

/**
 * @author songrubo
 * @version 2013年11月30日 下午12:09:51
 */
public class MapperProxyExt<T> implements InvocationHandler, Serializable {

	private static final long serialVersionUID = -6424540398559729838L;

	private final SqlSession sqlSession;

	private final Class<T> mapperInterface;

	private final Map<Method, MapperMethod> methodCache;

	private final MyBatisInterceptor[] interceptors;

	private final static ThreadLocal<MyBatisInvocation> myBatisInvocationHolder = new ThreadLocal<MyBatisInvocation>();


	public MapperProxyExt(
			SqlSession sqlSession,
			Class<T> mapperInterface,
			Map<Method, MapperMethod> methodCache,
			MyBatisInterceptor[] interceptors ) {
		this.sqlSession = sqlSession;
		this.mapperInterface = mapperInterface;
		this.methodCache = methodCache;
		this.interceptors = interceptors;
	}


	@Override
	public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
		if ( Object.class.equals(method.getDeclaringClass()) ) {
			return method.invoke(this, args);
		}
		try {

			MyBatisInvocation invocation = new MyBatisInvocation(mapperInterface, sqlSession, method, args,
					interceptors, methodCache);
			myBatisInvocationHolder.set(invocation);
			return invocation.execute();
		} finally {
			myBatisInvocationHolder.remove();
			PagingPlugin.removePage();
		}

	}


	public static MyBatisInvocation getMyBatisInvocation() {
		return myBatisInvocationHolder.get();
	}

}
