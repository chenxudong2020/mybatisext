/**
 * ListTypeTest.java com.ext Copyright (c) 2016, 山东翔龙集团版权所有.
 */

package com.ext;

import com.alibaba.fastjson.JSON;
import com.ext.mybatisext.activerecord.DB;
import com.ext.mybatisext.activerecord.MybatisExt;
import com.ext.mybatisext.activerecord.Record;

/**
 * TODO(这里用一句话描述这个类的作用)
 * <p>
 * TODO(这里描述这个类补充说明 – 可选)

 * @author   宋汝波
 * @date	 2016年5月18日 
 * @version  1.0.0	 
 */
public class ListTypeTest {

	public static void main( String[] args ) {
		DB dest = MybatisExt.open("org.postgresql.Driver", "jdbc:postgresql://192.168.50.205:5432/XL_ADMIN", "postgres", "postgres");
		Record r = dest.active("sys_city").one("select * from sys_city where id=426995350511812608");

		System.out.println(JSON.toJSONString(r));
		System.out.println(r.get("village").getClass().getName());

	}

}

