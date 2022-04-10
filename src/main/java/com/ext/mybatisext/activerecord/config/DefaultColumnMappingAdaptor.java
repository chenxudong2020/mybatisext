/**
 * ColumnMappingAdaptorImpl.java com.ext.mybatisext.test Copyright (c) 2015,
 * 北京微课创景教育科技有限公司版权所有.
 */

package com.ext.mybatisext.activerecord.config;

import com.ext.mybatisext.annotation.Column;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * 根据注解转换结果集 如果没有Column注解则直接 去掉数据库中的字段中”_“然后返回
 */
public class DefaultColumnMappingAdaptor implements ColumnMappingAdaptor {

	@Override
	public String adaptor( Class beanClass,String column ) {
		Field[] fields=beanClass.getDeclaredFields();
		for(Field field:fields){
			ReflectionUtils.makeAccessible(field);
			if(field.isAnnotationPresent(Column.class)){
				Column columnAnno=field.getAnnotation(Column.class);
				if(column.equals(columnAnno.value())){
					return field.getName();
				}

			};
		}

		return column.replace("_", "");

	}


}
