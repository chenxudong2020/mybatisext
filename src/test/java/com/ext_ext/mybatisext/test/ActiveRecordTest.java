package com.ext_ext.mybatisext.test;

import org.junit.Test;

import com.ext_ext.mybatisext.activerecord.DB;
import com.ext_ext.mybatisext.activerecord.MybatisExt;
import com.ext_ext.mybatisext.test.entity.User;


/**
 * <p>

 * @author   宋汝波
 * @date	 2015年8月31日 
 * @version  1.0.0	 
 */
public class ActiveRecordTest extends DaoTests {


	@Test
	public void test() {
		DB db = MybatisExt.open();

		User user = new User();
		user.setAge(100);
		user.setName("bobo");
		db.active("T_USER", User.class, "id", Long.class).getInsert().insert(user);
	}
}
