package com.ext_ext.activerecord.junit;

import com.ext_ext.mybatisext.annotation.TableName;


/**
 * <p>

 * @author   宋汝波
 * @date	 2015年8月25日 
 * @version  1.0.0	 
 */
@TableName(name = "person")
public class Person {

	Long id;

	String name;

	Integer age;


	public Long getId() {
		return id;
	}


	public void setId( Long id ) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName( String name ) {
		this.name = name;
	}


	public Integer getAge() {
		return age;
	}


	public void setAge( Integer age ) {
		this.age = age;
	}


}
