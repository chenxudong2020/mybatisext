package com.ext_ext.mybatisext.test;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ext_ext.mybatisext.test.mapper.UserMapper;

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

	@Before
	public void before() {
		String creatTable = "create table T_USER(id bigint primary key,name varchar(30),age int)";
		userMapper.update(creatTable);
	}
}
