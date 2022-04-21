package com.ext.mybatisext.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ext.mybatisext.helper.Page;
import com.ext.mybatisext.helper.PageImpl;
import com.ext.mybatisext.test.entity.User;

/**
 * 
 * <p>
 * 
 * @author 宋汝波
 * @date 2014年11月24日
 * @version 1.0.0
 */
public class PagingMapperTest extends DaoTests {

	@Before
	public void testInsert() {
		List<User> list = new ArrayList<User>();

		for ( int i = 0 ; i < 10 ; i++ ) {
			User user = new User();
			user.setUserAGE(1);
			user.setUSER_Name("li");
			user.setId((long) i);
			list.add(user);
		}

		userMapper.getInsert().insert(list);

		userMapper.selectByName("li");
	}


	@After
	public void testDelete() {
		for ( int i = 0 ; i < 10 ; i++ ) {
			userMapper.getDelete().deleteById((long) i);
		}
	}


	@Test
	public void testPaging() {
		Page<User> page = new PageImpl<User>(1, 10);
		page = userMapper.selectByNamePaging(page, "li");

		Assert.assertEquals(page.getCount(), 10);

	}
}
