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
		user.setUserAGE(100);
		user.setUSER_Name("bobo");
		db.active("T_USER", User.class, "id", Long.class).getInsert().insert(user);
	}
}
