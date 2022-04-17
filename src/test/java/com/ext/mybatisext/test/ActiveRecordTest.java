package com.ext.mybatisext.test;

import org.junit.Test;

import com.ext.mybatisext.test.entity.User;


/**
 * <p>

 * @author   宋汝波
 * @date	 2015年8月31日 
 * @version  1.0.0	 
 */
public class ActiveRecordTest extends DaoTests {


	@Test
	public void test() {
		//DB db = MybatisExt.open();

		User user = new User();
		user.setUser_AGE(100);
		user.setUser_NAME("bobo");
		userMapper.save(user);

	}
}
