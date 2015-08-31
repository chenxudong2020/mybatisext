package com.github.mybatisext.helper;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class ApplicationContentHolder implements ApplicationContextAware {

	private static ApplicationContext applicationContext;


	@Override
	public void setApplicationContext( ApplicationContext applicationContext ) {
		ApplicationContentHolder.applicationContext = applicationContext;
	}


	public static void setContext( ApplicationContext applicationContext ) {
		ApplicationContentHolder.applicationContext = applicationContext;
	}


	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}


	public static <T> T getBean( Class<T> cls ) {
		return applicationContext.getBean(cls);
	}
}
