package com.ext.mybatisext.test.entity;

import com.gitee.fastmybatis.annotation.Column;
import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import com.gitee.fastmybatis.core.support.Record;

import java.io.Serializable;


@Table(name = "t_user", pk = @Pk(name = "id"))
public class User implements Record {



	private Long id;



	private String user_NAME;

	private Integer user_AGE;

	public String getUser_NAME() {
		return user_NAME;
	}

	public void setUser_NAME(String user_NAME) {
		this.user_NAME = user_NAME;
	}

	public Integer getUser_AGE() {
		return user_AGE;
	}

	public void setUser_AGE(Integer user_AGE) {
		this.user_AGE = user_AGE;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
