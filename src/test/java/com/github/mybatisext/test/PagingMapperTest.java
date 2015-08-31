package com.github.mybatisext.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.mybatisext.helper.Page;
import com.github.mybatisext.test.entity.User;
import com.github.mybatisext.test.mapper.UserMapper;


/**
 * 
 * <p>

 * @author   宋汝波
 * @date	 2014年11月24日 
 * @version  1.0.0	 
 */
public class PagingMapperTest extends DaoTests {

	@Autowired
	UserMapper userMapper;


	@Before
	public void testInsert() {
		List<User> list = new ArrayList<User>();

		for ( int i = 0 ; i < 50 ; i++ ) {
			User user = new User();
			user.setAge(1);
			user.setName("li");
			user.setId((long) i);
			list.add(user);
		}

		userMapper.insertBatch(list);
	}


	@After
	public void testDelete() {
		for ( int i = 0 ; i < 50 ; i++ ) {
			userMapper.deleteById((long) i);
		}
	}


	@Test
	public void testPaging() {
		Page<User> page = new PageImpl<User>(1, 10);
		page = userMapper.selectByNamePaging(page, "li");

		Assert.assertEquals(page.getCount(), 50);


	}
}
