package com.github.mybatisext;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import com.github.mybatisext.helper.ApplicationContentHolder;
import com.github.mybatisext.interceptor.MyBatisInterceptor;
import com.github.mybatisext.interceptor.MyBatisInterceptorFactory;

/**
 * @author songrubo
 * @version 2013年11月30日 下午12:04:57
 */
public class MapperRegistryExt extends MapperRegistry {

	private static final Log logger = LogFactory.getLog(MapperRegistryExt.class);

	private Map<Class<?>, MapperProxyFactory<?>> knownMappers;

	MyBatisInterceptor[] interceptor;


	@SuppressWarnings("unchecked")
	public MapperRegistryExt( Configuration config ) {
		super(config);
		try {
			Field field = MapperRegistry.class.getDeclaredField("knownMappers");
			field.setAccessible(true);
			knownMappers = (Map<Class<?>, MapperProxyFactory<?>>) field.get(this);
		} catch ( Exception e ) {
			logger.error(e.getMessage(), e);
		}
		try {
			MyBatisInterceptorFactory myBatisExt = ApplicationContentHolder.getBean(MyBatisInterceptorFactory.class);
			interceptor = myBatisExt.getMyBatisInterceptor();
		} catch ( Exception e ) {
			// ignore
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public <T> T getMapper( Class<T> type, SqlSession sqlSession ) {
		final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
		if ( mapperProxyFactory == null ) {
			throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
		}
		try {
			Class<T> mapperInterface = mapperProxyFactory.getMapperInterface();
			final MapperProxyExt<T> mapperProxy = new MapperProxyExt<T>(sqlSession, mapperInterface,
					mapperProxyFactory.getMethodCache(), interceptor);
			return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[ ] { mapperInterface },
				mapperProxy);
		} catch ( Exception e ) {
			throw new BindingException("Error getting mapper instance. Cause: " + e, e);
		}
	}
}
