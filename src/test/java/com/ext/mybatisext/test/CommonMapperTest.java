package com.ext.mybatisext.test;

import java.util.ArrayList;
import java.util.List;

import com.gitee.fastmybatis.core.query.Query;
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
		user.setUser_AGE(12);
		user.setUser_NAME("bobo");
		userMapper.save(user);

		/*List<User> recordList = new ArrayList<User>();
		user = new User();
		user.setAge(126);
		user.setName("bobo1111");
		recordList.add(user);

		userMapper.saveBatch(recordList);
		user = new User();
		user.setAge(126);
		user.setName("bobo1111");


		System.out.println(1);*/

        Query query=new Query();
		query.eq("user_age",126).eq("user_name","bobo1111");
		List<User> list=userMapper.list(query);
		System.out.println(JSON.toJSONString(list));
	}
}
