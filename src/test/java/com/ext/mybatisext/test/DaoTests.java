package com.ext.mybatisext.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ext.mybatisext.activerecord.DB;
import com.ext.mybatisext.test.mapper.UserMapper;

/**
 * dao测试的基类
 * 
 * @Date 2014-5-16
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring.xml" })
public class DaoTests {

	@Autowired
	protected UserMapper userMapper;

	@Autowired
	protected DB db;


	@Before
	public void before() {
		String creatTable = "create table T_USER(id bigint identity primary key,user_name varchar(30),user_age int)";
		userMapper.update(creatTable);
	}


	@After
	public void after() {
		String creatTable = "drop table T_USER";
		userMapper.update(creatTable);
	}
}
