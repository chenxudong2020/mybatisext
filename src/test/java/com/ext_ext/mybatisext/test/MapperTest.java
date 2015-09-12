package com.ext_ext.mybatisext.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ext_ext.mybatisext.test.entity.User;

/**
 * 
 * <p>
 * 
 * @author 宋汝波
 * @date 2014年11月24日
 * @version 1.0.0
 */
public class MapperTest extends DaoTests {

	@Before
	public void testInsert() {
		User user = new User();
		user.setAge(1);
		user.setName("li");
		user.setId(1L);

		// 根据指定id插入
		userMapper.getInsert().insert(user);

		user = new User();
		user.setAge(13);
		user.setName("lei");
		// 自动生成id
		userMapper.getInsert().insert(user);
	}

	@After
	public void testDelete() {
		userMapper.getDelete().deleteById(1L);
	}

	@Test
	public void testSelectById() {
		User user = userMapper.getSelect().selectById(1L);
		Assert.assertEquals(user.getId().longValue(), 1L);
	}
}
