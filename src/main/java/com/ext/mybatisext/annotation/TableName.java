package com.ext.mybatisext.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 通用操作注解,加在实体或者mapper类上面
 * <p>
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@SuppressWarnings("rawtypes")
public @interface TableName {

	//表名称
	String name();


	//主键名称,默认id
	String id() default "id";


	//主键类型

	Class idType() default Long.class;


	//映射的实体名称
	Class type() default Void.class;


}
