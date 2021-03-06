package com.ext.mybatisext.test;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ext.mybatisext.activerecord.Record;
import com.ext.mybatisext.test.entity.User;

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
		user.setUserAGE(1);
		user.setUSER_Name("li");
		user.setId(1L);

		// 根据指定id插入
		userMapper.getInsert().insert(user);

		user = new User();
		user.setUserAGE(13);
		user.setUSER_Name("lei");
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


	@Test
	public void testDb() {
		List<Record> list = userMapper.getTableMeta().getDb().list("select * from T_USER");
		System.out.println(list.size());
	}
}
