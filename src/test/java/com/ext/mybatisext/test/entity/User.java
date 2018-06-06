package com.ext.mybatisext.test.entity;

import java.io.Serializable;

import com.ext.mybatisext.helper.Identity;

public class User extends Identity implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private Integer age;


	public void setName( String name ) {
		this.name = name;
	}


	public String getName() {
		return this.name;
	}


	public void setAge( Integer age ) {
		this.age = age;
	}


	public Integer getAge() {
		return this.age;
	}

}
