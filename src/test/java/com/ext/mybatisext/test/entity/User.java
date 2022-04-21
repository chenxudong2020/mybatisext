package com.ext.mybatisext.test.entity;

import java.io.Serializable;

import com.ext.mybatisext.annotation.Column;
import com.ext.mybatisext.helper.Identity;

public class User extends Identity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column("user_name")
	private String USER_Name;
	@Column("user_age")
	private Integer userAGE;


	public String getUSER_Name() {
		return USER_Name;
	}

	public void setUSER_Name(String USER_Name) {
		this.USER_Name = USER_Name;
	}

	public Integer getUserAGE() {
		return userAGE;
	}

	public void setUserAGE(Integer userAGE) {
		this.userAGE = userAGE;
	}
}
