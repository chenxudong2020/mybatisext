package com.ext.mybatisext.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.ext.mybatisext.test.entity.User;


/**
 * <p>

 * @author   宋汝波
 * @date	 2015年12月10日 
 * @version  1.0.0	 
 */
public class CommonMapperTest extends DaoTests {

	@Test
	public void test() {
		User user = new User();
		user.setAge(12);
		user.setName("bobo");
		userMapper.insertSelective(user);

		List<User> recordList = new ArrayList<User>();
		user = new User();
		user.setAge(126);
		user.setName("bobo1111");
		recordList.add(user);

		userMapper.insertSelectiveBatch(recordList);
		user = new User();
		user.setAge(126);
		user.setName("bobo1111");
		int count = userMapper.count(user);

		System.out.println(count);

		user = userMapper.selectOne(user);
		System.out.println(JSON.toJSONString(user));
	}
}
